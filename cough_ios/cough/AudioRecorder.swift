//
//  AudioRecorder.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import SwiftUI
import AVFoundation
import Firebase
import FirebaseAuth
import FirebaseFunctions

class AudioRecorder: NSObject, ObservableObject {
    @Published var isRecording = false
    @Published var recordingTime: TimeInterval = 0
    @Published var audioLevel: Float = 0
    @Published var isAnalyzing = false
    @Published var analysisResult: CoughAnalysisResult?
    @Published var errorMessage: String?
    
    private var audioRecorder: AVAudioRecorder?
    private var audioSession: AVAudioSession?
    private var recordingTimer: Timer?
    private var levelTimer: Timer?
    private let functions = Functions.functions(region: "us-central1")
    
    override init() {
        super.init()
        // Don't setup audio session immediately - wait until recording
    }
    
    private func setupAudioSession() {
        audioSession = AVAudioSession.sharedInstance()
        
        do {
            try audioSession?.setCategory(.playAndRecord, mode: .default)
            try audioSession?.setActive(true)
        } catch {
            print("Failed to set up audio session: \(error)")
            errorMessage = "Failed to set up audio recording"
        }
    }
    
    private func requestMicrophonePermission(completion: @escaping (Bool) -> Void) {
        // Request microphone permission
        if #available(iOS 17.0, *) {
            AVAudioApplication.requestRecordPermission { allowed in
                DispatchQueue.main.async {
                    if !allowed {
                        self.errorMessage = "Microphone access is required to analyze your cough"
                    }
                    completion(allowed)
                }
            }
        } else {
            audioSession?.requestRecordPermission { allowed in
                DispatchQueue.main.async {
                    if !allowed {
                        self.errorMessage = "Microphone access is required to analyze your cough"
                    }
                    completion(allowed)
                }
            }
        }
    }
    
    func startRecording() {
        // First setup audio session if not already done
        if audioSession == nil {
            setupAudioSession()
        }
        
        // Request permission first
        requestMicrophonePermission { [weak self] allowed in
            guard allowed else { return }
            
            self?.performRecording()
        }
    }
    
    private func performRecording() {
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let audioFilename = documentsPath.appendingPathComponent("cough_\(Date().timeIntervalSince1970).m4a")
        
        let settings = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 44100,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
        ]
        
        do {
            audioRecorder = try AVAudioRecorder(url: audioFilename, settings: settings)
            audioRecorder?.delegate = self
            audioRecorder?.isMeteringEnabled = true
            audioRecorder?.record()
            
            isRecording = true
            recordingTime = 0
            
            // Start recording timer
            recordingTimer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
                self?.recordingTime += 0.1
                
                // Auto-stop after 10 seconds
                if self?.recordingTime ?? 0 >= 10 {
                    self?.stopRecording()
                }
            }
            
            // Start level monitoring
            levelTimer = Timer.scheduledTimer(withTimeInterval: 0.05, repeats: true) { [weak self] _ in
                self?.audioRecorder?.updateMeters()
                let level = self?.audioRecorder?.averagePower(forChannel: 0) ?? -160
                self?.audioLevel = max(0, (level + 160) / 160)
            }
        } catch {
            print("Failed to start recording: \(error)")
            errorMessage = "Failed to start recording"
        }
    }
    
    func stopRecording() {
        print("🛑 Stopping recording...")
        print("📊 Recording duration: \(recordingTime) seconds")
        audioRecorder?.stop()
        recordingTimer?.invalidate()
        levelTimer?.invalidate()
        isRecording = false
        audioLevel = 0
        
        // Process the recording
        if let url = audioRecorder?.url {
            print("📁 Recording saved at: \(url)")
            print("📁 File exists: \(FileManager.default.fileExists(atPath: url.path))")
            processRecording(at: url)
        } else {
            print("❌ No recording URL found")
        }
    }
    
    private func processRecording(at url: URL) {
        Task {
            await analyzeAudio(at: url)
        }
    }
    
    private func analyzeAudio(at url: URL) async {
        print("🔍 Starting audio analysis...")
        
        await MainActor.run {
            isAnalyzing = true
            errorMessage = nil
        }
        
        do {
            // Read audio file
            let audioData = try Data(contentsOf: url)
            print("📊 Audio file size: \(audioData.count) bytes")
            let base64Audio = audioData.base64EncodedString()
            
            // Get current user
            guard let userId = Auth.auth().currentUser?.uid else {
                throw NSError(domain: "AudioRecorder", code: 401, userInfo: [NSLocalizedDescriptionKey: "User not authenticated"])
            }
            print("👤 User ID: \(userId)")
            
            // Prepare request data
            let requestData: [String: Any] = [
                "userId": userId,
                "audioData": base64Audio,
                "audioFormat": "m4a",
                "duration": round(recordingTime * 10) / 10,  // Round to 1 decimal place
                "metadata": [
                    "recordedAt": Date().timeIntervalSince1970,
                    "platform": "iOS",
                    "appVersion": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
                ]
            ]
            
            print("☁️ Calling Cloud Function: analyzeCough")
            print("📤 Request data size: \(requestData.description.count) characters")
            
            // Call Cloud Function
            let result = try await functions.httpsCallable("analyzeCough").call(requestData)
            
            print("✅ Cloud Function response received")
            print("📥 Response type: \(type(of: result.data))")
            
            // Parse result
            if let data = result.data as? [String: Any] {
                print("📋 Response data: \(data)")
                let analysisResult = try parseAnalysisResult(from: data)
            
                await MainActor.run {
                    self.analysisResult = analysisResult
                    self.isAnalyzing = false
                    print("🎉 Analysis complete!")
                }
                
                // Clean up audio file
                try? FileManager.default.removeItem(at: url)
            } else {
                print("❌ Invalid response format")
            }
        } catch {
            print("❌ Analysis error: \(error)")
            print("❌ Error type: \(type(of: error))")
            print("❌ Error details: \(error.localizedDescription)")
            
            await MainActor.run {
                self.errorMessage = "Analysis failed: \(error.localizedDescription)"
                self.isAnalyzing = false
            }
        }
    }
    
    private func parseAnalysisResult(from data: [String: Any]) throws -> CoughAnalysisResult {
        guard let results = data["results"] as? [String: Any],
              let insights = data["insights"] as? [String: Any] else {
            throw NSError(domain: "AudioRecorder", code: 500, userInfo: [NSLocalizedDescriptionKey: "Invalid response format"])
        }
        
        return CoughAnalysisResult(
            analysisId: data["analysisId"] as? String ?? "",
            timestamp: data["timestamp"] as? Double ?? Date().timeIntervalSince1970,
            coughType: results["coughType"] as? String ?? "unknown",
            severity: results["severity"] as? String ?? "mild",
            characteristics: results["characteristics"] as? [String] ?? [],
            potentialCauses: parsePotentialCauses(from: results["potentialCauses"] as? [[String: Any]] ?? []),
            managementApproaches: results["managementApproaches"] as? [String] ?? [],
            urgency: results["urgency"] as? String ?? "routine",
            confidence: results["confidence"] as? Double ?? 0.5,
            soundPattern: insights["soundPattern"] as? String ?? "",
            frequency: insights["frequency"] as? String ?? "",
            duration: insights["duration"] as? String ?? "",
            additionalNotes: insights["additionalNotes"] as? [String] ?? []
        )
    }
    
    private func parsePotentialCauses(from data: [[String: Any]]) -> [PotentialCause] {
        return data.compactMap { cause in
            guard let condition = cause["condition"] as? String,
                  let likelihood = cause["likelihood"] as? String,
                  let description = cause["description"] as? String else {
                return nil
            }
            return PotentialCause(condition: condition, likelihood: likelihood, description: description)
        }
    }
}

extension AudioRecorder: AVAudioRecorderDelegate {
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        if !flag {
            errorMessage = "Recording failed"
        }
    }
}

// Data Models
struct CoughAnalysisResult: Identifiable, Equatable {
    let id = UUID()
    let analysisId: String
    let timestamp: Double
    let coughType: String
    let severity: String
    let characteristics: [String]
    let potentialCauses: [PotentialCause]
    let managementApproaches: [String]
    let urgency: String
    let confidence: Double
    let soundPattern: String
    let frequency: String
    let duration: String
    let additionalNotes: [String]
    
    static func == (lhs: CoughAnalysisResult, rhs: CoughAnalysisResult) -> Bool {
        lhs.id == rhs.id
    }
    
    var urgencyColor: Color {
        switch urgency {
        case "urgent":
            return .red
        case "soon":
            return .orange
        default:
            return .green
        }
    }
    
    var severityColor: Color {
        switch severity {
        case "severe":
            return .red
        case "moderate":
            return .orange
        default:
            return .green
        }
    }
}

struct PotentialCause: Identifiable, Equatable {
    let id = UUID()
    let condition: String
    let likelihood: String
    let description: String
    
    var likelihoodColor: Color {
        switch likelihood {
        case "high":
            return .red
        case "medium":
            return .orange
        default:
            return .green
        }
    }
}