package com.twintipsolutions.cough.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class PotentialCause(
    val id: String = UUID.randomUUID().toString(),
    val condition: String,
    val likelihood: String,
    val description: String
) {
    val likelihoodColor: Color
        get() = when (likelihood) {
            "high" -> Color.Red
            "medium" -> Color(0xFFFFA500) // Orange
            else -> Color.Green
        }
}
