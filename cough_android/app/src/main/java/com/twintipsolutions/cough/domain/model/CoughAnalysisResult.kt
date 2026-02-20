package com.twintipsolutions.cough.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class CoughAnalysisResult(
    val id: String = UUID.randomUUID().toString(),
    val analysisId: String,
    val timestamp: Double,
    val coughType: String,
    val severity: String,
    val characteristics: List<String>,
    val potentialCauses: List<PotentialCause>,
    val managementApproaches: List<String>,
    val urgency: String,
    val confidence: Double,
    val soundPattern: String,
    val frequency: String,
    val duration: String,
    val additionalNotes: List<String>
) {
    val severityColor: Color
        get() = when (severity) {
            "severe" -> Color.Red
            "moderate" -> Color(0xFFFFA500) // Orange
            else -> Color.Green
        }

    val urgencyColor: Color
        get() = when (urgency) {
            "urgent" -> Color.Red
            "soon" -> Color(0xFFFFA500) // Orange
            else -> Color.Green
        }
}
