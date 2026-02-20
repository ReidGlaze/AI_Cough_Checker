package com.twintipsolutions.cough.domain.recorder

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.twintipsolutions.cough.domain.model.CoughAnalysisResult
import com.twintipsolutions.cough.domain.model.PotentialCause
import com.twintipsolutions.cough.ui.components.AppReviewManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class AudioRecorder(private val context: Context) {
    companion object {
        private const val TAG = "AudioRecorder"
        private const val MAX_RECORDING_DURATION = 10000L
    }

    private val functions = FirebaseFunctions.getInstance("us-central1")
    private val auth = FirebaseAuth.getInstance()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var recordingStartTime: Long = 0

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingTime = MutableStateFlow(0L)
    val recordingTime: StateFlow<Long> = _recordingTime.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<CoughAnalysisResult?>(null)
    val analysisResult: StateFlow<CoughAnalysisResult?> = _analysisResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun hasRecordingPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun startRecording() {
        if (!hasRecordingPermission()) {
            _errorMessage.value = "Microphone permission is required to analyze your cough"
            return
        }

        withContext(Dispatchers.IO) {
            try {
                setupRecording()
                mediaRecorder?.start()
                recordingStartTime = System.currentTimeMillis()
                _isRecording.value = true
                _recordingTime.value = 0L
                _errorMessage.value = null

                startRecordingTimer()
                Log.d(TAG, "Recording started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start recording", e)
                _errorMessage.value = "Failed to start recording: ${e.message}"
            }
        }
    }

    private suspend fun startRecordingTimer() {
        withContext(Dispatchers.IO) {
            while (_isRecording.value) {
                val elapsed = System.currentTimeMillis() - recordingStartTime
                _recordingTime.value = elapsed
                updateAudioLevel()

                if (elapsed >= MAX_RECORDING_DURATION) {
                    stopRecording()
                    break
                }
                delay(100)
            }
        }
    }

    private fun updateAudioLevel() {
        if (!_isRecording.value) {
            _audioLevel.value = 0f
        } else {
            _audioLevel.value = (Math.random() * 0.3f + 0.1f).toFloat()
        }
    }

    private fun setupRecording() {
        audioFile = File(context.cacheDir, "cough_${System.currentTimeMillis()}.m4a")

        mediaRecorder = if (Build.VERSION.SDK_INT >= 31) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioChannels(1)
            setAudioEncodingBitRate(128000)
            setOutputFile(audioFile?.absolutePath)
            prepare()
        }
    }

    suspend fun stopRecording() {
        withContext(Dispatchers.IO) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                _isRecording.value = false
                _audioLevel.value = 0f

                Log.d(TAG, "Recording stopped. Duration: ${_recordingTime.value}ms")

                audioFile?.let { file ->
                    if (file.exists()) {
                        Log.d(TAG, "Audio file saved: ${file.absolutePath}, size: ${file.length()} bytes")
                        analyzeAudio(file)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recording", e)
                _errorMessage.value = "Failed to stop recording: ${e.message}"
            }
        }
    }

    private suspend fun analyzeAudio(file: File) {
        _isAnalyzing.value = true
        _analysisResult.value = null

        try {
            val audioBytes = file.readBytes()
            val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

            val userId = auth.currentUser?.uid ?: ""
            val data = hashMapOf(
                "audio" to base64Audio,
                "userId" to userId,
                "platform" to "android",
                "appVersion" to getAppVersion()
            )

            val result = functions
                .getHttpsCallable("analyzeCough")
                .call(data)
                .continueWith { task ->
                    @Suppress("UNCHECKED_CAST")
                    task.result?.data as? Map<String, Any>
                }
                .addOnSuccessListener { response ->
                    if (response != null) {
                        try {
                            val analysisResult = parseAnalysisResult(response)
                            _analysisResult.value = analysisResult
                            // Trigger review prompt after successful analysis
                            AppReviewManager.incrementAnalysisCount(context)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse analysis result", e)
                            _errorMessage.value = "Failed to parse analysis: ${e.message}"
                        }
                    }
                    _isAnalyzing.value = false
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Analysis failed", e)
                    _errorMessage.value = "Analysis failed: ${e.message}"
                    _isAnalyzing.value = false
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze audio", e)
            _errorMessage.value = "Failed to analyze: ${e.message}"
            _isAnalyzing.value = false
        }
    }

    private fun getAppVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseAnalysisResult(map: Map<String, Any>): CoughAnalysisResult {
        val results = map["results"] as? Map<String, Any>
            ?: throw Exception("Invalid response format: missing results")
        val insights = map["insights"] as? Map<String, Any>
            ?: throw Exception("Invalid response format: missing insights")

        val analysisId = (map["analysisId"] as? String) ?: ""
        val timestamp = (map["timestamp"] as? Double) ?: (System.currentTimeMillis() / 1000.0)

        val coughType = (results["coughType"] as? String) ?: "unknown"
        val severity = (results["severity"] as? String) ?: "mild"
        val characteristics = (results["characteristics"] as? List<*>)
            ?.filterIsInstance<String>() ?: emptyList()
        val potentialCauses = parsePotentialCauses(
            (results["potentialCauses"] as? List<*>) ?: emptyList()
        )
        val managementApproaches = (results["managementApproaches"] as? List<*>)
            ?.filterIsInstance<String>() ?: emptyList()
        val urgency = (results["urgency"] as? String) ?: "routine"
        val confidence = (results["confidence"] as? Double) ?: 0.5

        val soundPattern = (insights["soundPattern"] as? String) ?: ""
        val frequency = (insights["frequency"] as? String) ?: ""
        val duration = (insights["duration"] as? String) ?: ""
        val additionalNotes = (insights["additionalNotes"] as? List<*>)
            ?.filterIsInstance<String>() ?: emptyList()

        return CoughAnalysisResult(
            analysisId = analysisId,
            timestamp = timestamp,
            coughType = coughType,
            severity = severity,
            characteristics = characteristics,
            potentialCauses = potentialCauses,
            managementApproaches = managementApproaches,
            urgency = urgency,
            confidence = confidence,
            soundPattern = soundPattern,
            frequency = frequency,
            duration = duration,
            additionalNotes = additionalNotes
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePotentialCauses(list: List<*>): List<PotentialCause> {
        return list.mapNotNull { item ->
            (item as? Map<String, Any>)?.let { map ->
                PotentialCause(
                    condition = (map["condition"] as? String) ?: "",
                    likelihood = (map["likelihood"] as? String) ?: "",
                    description = (map["description"] as? String) ?: ""
                )
            }
        }
    }

    fun cleanup() {
        mediaRecorder?.release()
        mediaRecorder = null
        audioFile?.delete()
    }
}
