import { onCall, HttpsError } from 'firebase-functions/v2/https';
import { initializeApp } from 'firebase-admin/app';
import { getFirestore, FieldValue } from 'firebase-admin/firestore';
import * as logger from 'firebase-functions/logger';
import { GoogleGenAI } from '@google/genai';

// Initialize Firebase Admin
initializeApp();
const db = getFirestore();

// Model configuration - Gemini 3.1 Pro Preview with fallback to 2.5 Flash
const GEMINI_3_MODEL = 'gemini-3.1-pro-preview';
const GEMINI_2_MODEL = 'gemini-2.5-flash';
const USE_GEMINI_3 = process.env.USE_GEMINI_3 !== 'false'; // Enable by default, set USE_GEMINI_3=false to use 2.5

// Initialize the new Google GenAI SDK with Vertex AI backend
const projectId = process.env.GCLOUD_PROJECT || 'cough-f9960';

// Gemini 3 requires global location, Gemini 2.5 uses us-central1
const genAI_Global = new GoogleGenAI({
  vertexai: true,
  project: projectId,
  location: 'global',
});

const genAI_Regional = new GoogleGenAI({
  vertexai: true,
  project: projectId,
  location: 'us-central1',
});

const systemInstruction = 'You are a medical cough detector. Your PRIMARY job is to distinguish between ACTUAL COUGHS and other sounds. A cough is an explosive expulsion from lungs with a distinct sound. Speech, talking, singing, humming, breathing, or other vocalizations are NOT coughs. If unsure, report "no cough detected". NEVER analyze speech as a cough.';

// Define types
interface CoughAnalysisRequest {
  userId: string;
  audioData: string; // Base64 encoded audio
  audioFormat: string; // e.g., 'm4a', 'wav'
  duration: number; // Duration in seconds
  metadata?: {
    symptoms?: string[];
    recentIllness?: boolean;
    smoker?: boolean;
    age?: number;
  };
}

interface CoughAnalysisResponse {
  analysisId: string;
  timestamp: number;
  results: {
    coughType: 'dry' | 'wet' | 'productive' | 'barking' | 'whooping' | 'unknown';
    severity: 'mild' | 'moderate' | 'severe';
    characteristics: string[];
    potentialCauses: Array<{
      condition: string;
      likelihood: 'low' | 'medium' | 'high';
      description: string;
    }>;
    managementApproaches: string[];
    urgency: 'routine' | 'soon' | 'urgent';
    confidence: number; // 0-1
  };
  insights: {
    soundPattern: string;
    frequency: string;
    duration: string;
    additionalNotes: string[];
  };
}

export const analyzeCough = onCall<CoughAnalysisRequest>(
  {
    region: 'us-central1',
    cors: true,
    maxInstances: 100,
    memory: '1GiB',
    timeoutSeconds: 540,
  },
  async (request) => {
    try {
      // Validate authentication
      if (!request.auth) {
        throw new HttpsError('unauthenticated', 'User must be authenticated');
      }

      const { userId, audioData, audioFormat, duration, metadata } = request.data;

      // Validate request data
      if (!userId || !audioData || !audioFormat || !duration) {
        throw new HttpsError('invalid-argument', 'Missing required fields');
      }

      // Validate user matches auth
      if (userId !== request.auth.uid) {
        throw new HttpsError('permission-denied', 'User ID mismatch');
      }

      logger.info(`Starting cough analysis for user: ${userId}`);

      // Convert audio data to buffer for analysis
      const timestamp = Date.now();
      const audioBuffer = Buffer.from(audioData, 'base64');
      
      // Optional: Save audio to Cloud Storage (commented out for privacy)
      // If you want to enable audio storage, uncomment the following:
      /*
      try {
        const audioFileName = `cough-recordings/${userId}/${timestamp}.${audioFormat}`;
        const bucket = storage.bucket(); // Uses default bucket
        const file = bucket.file(audioFileName);
        
        await file.save(audioBuffer, {
          metadata: {
            contentType: `audio/${audioFormat}`,
            metadata: {
              userId,
              timestamp: timestamp.toString(),
              duration: duration.toString(),
            },
          },
        });
        logger.info(`Audio saved to Cloud Storage: ${audioFileName}`);
      } catch (storageError) {
        logger.error('Failed to save audio to Cloud Storage:', storageError);
        // Continue with analysis even if storage fails
      }
      */

      // Analyze cough using Vertex AI
      const analysis = await analyzeCoughWithAI(audioBuffer, audioFormat, duration, metadata);

      // Create analysis document
      const analysisId = db.collection('analyses').doc().id;
      const analysisDoc: CoughAnalysisResponse = {
        analysisId,
        timestamp,
        results: analysis.results,
        insights: analysis.insights,
      };

      // Save to Firestore (without audio path since we're not storing audio)
      await db.collection('users').doc(userId).collection('analyses').doc(analysisId).set({
        ...analysisDoc,
        metadata,
      });

      // Update user's last analysis timestamp
      await db.collection('users').doc(userId).update({
        lastAnalysisAt: timestamp,
        totalAnalyses: FieldValue.increment(1),
      });

      logger.info(`Cough analysis completed for user: ${userId}, analysis: ${analysisId}`);

      return analysisDoc;
    } catch (error) {
      logger.error('Error in cough analysis:', error);
      
      if (error instanceof HttpsError) {
        throw error;
      }
      
      throw new HttpsError('internal', 'An error occurred during analysis');
    }
  }
);

// Analyze cough using Vertex AI
async function analyzeCoughWithAI(
  audioBuffer: Buffer,
  audioFormat: string,
  duration: number,
  _metadata?: any
): Promise<{ results: any; insights: any }> {
  try {
    logger.info(`Starting AI analysis with Vertex AI (${USE_GEMINI_3 ? GEMINI_3_MODEL : GEMINI_2_MODEL})`);
    
    // Pre-check: Very short recordings are likely noise/silence
    if (duration < 0.5) {
      logger.info('Recording too short, likely no cough');
      return {
        results: {
          coughType: 'none',
          severity: 'none',
          characteristics: ['Recording too short'],
          potentialCauses: [],
          managementApproaches: ['Please record a longer cough sound'],
          urgency: 'none',
          confidence: 1.0
        },
        insights: {
          soundPattern: 'Recording too brief to analyze',
          frequency: 'N/A',
          duration: `${duration.toFixed(1)} seconds`,
          additionalNotes: ['Minimum 0.5 seconds needed for analysis']
        }
      };
    }

    // Convert audio buffer to base64 for Gemini
    const audioBase64 = audioBuffer.toString('base64');

    // Clear prompt with better cough detection
    const prompt = `CRITICAL: You must determine if this audio contains an ACTUAL COUGH.

A COUGH is:
- An explosive expulsion of air from lungs
- Has a distinct "cough" sound pattern
- NOT talking, singing, humming, or other vocalizations
- NOT breathing, sighing, or clearing throat gently

If you hear:
- SILENCE: return {"noCoughDetected":true,"message":"No sound detected"}
- SPEECH/TALKING: return {"noCoughDetected":true,"message":"Speech detected, not a cough"}
- OTHER SOUNDS: return {"noCoughDetected":true,"message":"Non-cough sound detected"}

ONLY if you hear an ACTUAL MEDICAL COUGH, analyze it:

Severity (be conservative - err on the side of lower severity):
- MILD (60% of coughs): Light cough, voluntary cough, single coughs, throat clearing
- MODERATE (35% of coughs): Typical sick person cough, multiple coughs, productive cough, mild wheeze
- SEVERE (5% of coughs): RARE - only for extreme distress, uncontrollable coughing fits, gasping for air, stridor

Type (based on sound quality):
- DRY: No mucus, scratchy sound
- WET: Mucus/fluid sounds
- PRODUCTIVE: Clearing mucus
- BARKING: Seal-like sound

If cough detected, return (default to routine urgency unless severe):
{"results":{"coughType":"[actual type]","severity":"[actual severity]","characteristics":["[trait 1]","[trait 2]"],"potentialCauses":[{"condition":"[condition]","likelihood":"high|medium|low","description":"[description]"}],"managementApproaches":["[approach 1]","[approach 2]"],"urgency":"routine|soon|urgent","confidence":0.5},"insights":{"soundPattern":"[what you hear]","frequency":"single","duration":"${duration.toFixed(1)}s","additionalNotes":["[note]"]}}

Urgency guide:
- routine: Most coughs (can wait for regular appointment)
- soon: Persistent cough with concerning features
- urgent: ONLY if severe distress, difficulty breathing, or choking`;

    // Build request contents for new SDK
    const contents = [
      {
        role: 'user' as const,
        parts: [
          { text: systemInstruction + '\n\n' + prompt },
          {
            inlineData: {
              mimeType: `audio/${audioFormat === 'm4a' ? 'mp4' : audioFormat}`,
              data: audioBase64,
            },
          },
        ],
      },
    ];

    // Generation config
    const config: any = {
      temperature: 0.3,
      maxOutputTokens: 4000,
      topP: 0.95,
    };

    // Add thinking config for Gemini 3 (improves medical analysis accuracy)
    // Using 'low' for balance between speed and accuracy (options: minimal, low, medium, high)
    if (USE_GEMINI_3) {
      config.thinkingConfig = {
        thinkingLevel: 'LOW',
      };
    }

    // Select the appropriate client and model
    const primaryClient = USE_GEMINI_3 ? genAI_Global : genAI_Regional;
    const primaryModel = USE_GEMINI_3 ? GEMINI_3_MODEL : GEMINI_2_MODEL;

    // Send audio to Gemini with native audio support
    let response;
    try {
      response = await primaryClient.models.generateContent({
        model: primaryModel,
        contents,
        config,
      });
    } catch (modelError) {
      // Fallback to Gemini 2.5 if Gemini 3 fails
      if (USE_GEMINI_3) {
        logger.warn('Gemini 3 failed, falling back to Gemini 2.5:', modelError);
        const fallbackConfig = { ...config };
        delete fallbackConfig.thinkingConfig; // Remove Gemini 3 specific config
        response = await genAI_Regional.models.generateContent({
          model: GEMINI_2_MODEL,
          contents,
          config: fallbackConfig,
        });
      } else {
        throw modelError;
      }
    }
    const text = response.text || '';
    
    logger.info('AI response received');
    logger.info('AI response text:', text);

    // Parse the JSON response
    try {
      const jsonMatch = text.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        const analysisData = JSON.parse(jsonMatch[0]);
        
        // Check if no cough was detected
        if (analysisData.noCoughDetected) {
          logger.info('No cough detected in recording');
          return {
            results: {
              coughType: 'none',
              severity: 'none',
              characteristics: ['No cough detected'],
              potentialCauses: [],
              managementApproaches: ['Please record a clear cough sound for analysis'],
              urgency: 'none',
              confidence: 1.0
            },
            insights: {
              soundPattern: 'No cough pattern detected',
              frequency: 'N/A',
              duration: `${duration.toFixed(1)} seconds`,
              additionalNotes: [analysisData.message || 'No cough detected in this recording']
            }
          };
        }
        
        return analysisData;
      }
    } catch (parseError) {
      logger.error('Failed to parse AI response:', parseError);
    }

    // Fallback to structured response if parsing fails
    return generateStructuredAnalysis(duration, text);
  } catch (error) {
    logger.error('Vertex AI analysis failed:', error);
    throw new HttpsError('internal', 'AI analysis failed. Please try again.');
  }
}

// Generate structured analysis from AI text response
function generateStructuredAnalysis(duration: number, aiResponse: string): { results: any; insights: any } {
  // Extract key information from AI response using simple pattern matching
  const coughType = aiResponse.match(/(?:dry|wet|productive|barking|whooping)/i)?.[0]?.toLowerCase() || 'unknown';
  const severity = aiResponse.match(/(?:mild|moderate|severe)/i)?.[0]?.toLowerCase() || 'unknown';
  
  // Return basic potential causes based on cough type
  const potentialCauses = coughType === 'dry' ? [
    { condition: 'Post-viral Cough', likelihood: 'medium', description: 'Common after respiratory infections' },
    { condition: 'Allergies', likelihood: 'medium', description: 'Environmental triggers may be present' },
    { condition: 'GERD', likelihood: 'low', description: 'Acid reflux can cause chronic dry cough' }
  ] : coughType === 'wet' || coughType === 'productive' ? [
    { condition: 'Common Cold', likelihood: 'high', description: 'Most frequent cause of acute cough' },
    { condition: 'Bronchitis', likelihood: 'medium', description: 'Inflammation of bronchial tubes' },
    { condition: 'Sinusitis', likelihood: 'low', description: 'Post-nasal drip causing cough' }
  ] : [
    { condition: 'Various Conditions', likelihood: 'medium', description: 'Unable to determine specific cause from recording' },
    { condition: 'Environmental Factors', likelihood: 'medium', description: 'Consider air quality and allergens' }
  ];

  return {
    results: {
      coughType: coughType as any,
      severity: severity as any,
      characteristics: [
        'Unable to determine specific characteristics',
        `Duration: ${duration.toFixed(1)} seconds`
      ],
      potentialCauses,
      managementApproaches: [
        'Recording quality may affect analysis accuracy',
        'Quiet environments are generally preferred for audio clarity',
        'Healthcare consultation is commonly advised for persistent symptoms'
      ],
      urgency: 'routine',
      confidence: 0.3
    },
    insights: {
      soundPattern: 'Unable to analyze pattern clearly',
      frequency: 'Single recording',
      duration: `${duration.toFixed(1)} seconds`,
      additionalNotes: [
        'Analysis was incomplete',
        'Try recording again for better results'
      ]
    }
  };
}

// Get analysis history
export const getAnalysisHistory = onCall(
  {
    region: 'us-central1',
    cors: true,
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError('unauthenticated', 'User must be authenticated');
    }

    const userId = request.auth.uid;
    const limit = request.data.limit || 10;

    try {
      const analyses = await db
        .collection('users')
        .doc(userId)
        .collection('analyses')
        .orderBy('timestamp', 'desc')
        .limit(limit)
        .get();

      const history = analyses.docs.map(doc => ({
        id: doc.id,
        ...doc.data(),
      }));

      return { history };
    } catch (error) {
      logger.error('Error fetching analysis history:', error);
      throw new HttpsError('internal', 'Failed to fetch history');
    }
  }
);

