package com.twintipsolutions.cough.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twintipsolutions.cough.domain.model.CoughAnalysisResult
import com.twintipsolutions.cough.domain.model.PotentialCause
import com.twintipsolutions.cough.ui.components.AnimatedGradientBackground
import com.twintipsolutions.cough.ui.components.GlassmorphicCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CoughAnalysisResultView(
    result: CoughAnalysisResult,
    onClose: () -> Unit
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var reportText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1928))
    ) {
        AnimatedGradientBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Spacer(modifier = Modifier.height(48.dp))

            // Header icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (result.coughType == "none") Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (result.coughType == "none") Icons.Default.Warning
                    else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (result.coughType == "none") "No Cough Detected" else "Analysis Complete",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatTimestamp(result.timestamp),
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main results card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                if (result.coughType != "none") {
                    // Type and Severity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ResultCard(
                            title = "Type",
                            value = result.coughType.replaceFirstChar { it.titlecase() },
                            icon = Icons.Default.GraphicEq,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                        ResultCard(
                            title = "Severity",
                            value = result.severity.replaceFirstChar { it.titlecase() },
                            icon = Icons.Default.Info,
                            color = result.severityColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Urgency + Confidence
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = result.urgencyColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Medical Attention",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = result.urgency.replaceFirstChar { it.titlecase() },
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${(result.confidence * 100).toInt()}% Confidence",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // No cough detected message
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No cough was detected in this recording",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please record a clear cough sound for analysis",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Characteristics
            if (result.characteristics.isNotEmpty() && result.coughType != "none") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Characteristics",
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (row in result.characteristics.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (chip in row) {
                                    CharacteristicChip(
                                        text = chip,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size == 1) {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Potential Causes
            if (result.potentialCauses.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Potential Causes",
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    for (cause in result.potentialCauses) {
                        PotentialCauseCard(cause = cause)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Management Approaches
            if (result.managementApproaches.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Commonly Discussed Management Approaches",
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    for (approach in result.managementApproaches) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Text(
                                text = approach,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 15.sp
                            )
                        }
                    }
                    Text(
                        text = "These are general approaches discussed in medical literature. Always consult a healthcare provider for personalized advice.",
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sound Analysis
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sound Analysis",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                SoundAnalysisRow(label = "Pattern", value = result.soundPattern)
                SoundAnalysisRow(label = "Frequency", value = result.frequency)
                SoundAnalysisRow(label = "Duration", value = result.duration)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Additional Notes
            if (result.additionalNotes.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Additional Notes",
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    for (note in result.additionalNotes) {
                        Text(
                            text = "• $note",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(vertical = 2.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Disclaimer
            Text(
                text = "This analysis is for informational purposes only and should not replace professional medical advice. Please consult a healthcare provider for proper diagnosis and treatment.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Medical References
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Medical References",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    MedicalReferenceItem("MedlinePlus - NIH Health Information") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://medlineplus.gov/")))
                    }
                    MedicalReferenceItem("CDC - Centers for Disease Control") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cdc.gov/")))
                    }
                    MedicalReferenceItem("NCBI - National Center for Biotechnology") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ncbi.nlm.nih.gov/")))
                    }
                    MedicalReferenceItem("NHS - UK National Health Service") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nhs.uk/")))
                    }
                    MedicalReferenceItem("WHO - World Health Organization") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.who.int/")))
                    }
                }
                Text(
                    text = "AI analysis is based on patterns from medical literature. Sources are provided for educational reference only.",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Report Button
            TextButton(
                onClick = { showReportDialog = true },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Report harmful content",
                    color = Color.Red.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }

        // Close button overlay
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(30.dp)
            )
        }
    }

    // Report dialog
    if (showReportDialog) {
        ReportDialog(
            reportText = reportText,
            onReportTextChange = { reportText = it },
            onDismiss = { showReportDialog = false },
            onSubmit = {
                submitReport(result.analysisId, reportText, result)
                reportText = ""
                showReportDialog = false
            }
        )
    }
}

@Composable
private fun ResultCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CharacteristicChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun PotentialCauseCard(cause: PotentialCause) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(cause.likelihoodColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = cause.condition,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${cause.likelihood})",
                    color = cause.likelihoodColor,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cause.description,
                modifier = Modifier.padding(start = 20.dp),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun SoundAnalysisRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun MedicalReferenceItem(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ReportDialog(
    reportText: String,
    onReportTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Harmful Content") },
        text = {
            Column {
                Text(
                    text = "Please describe why you believe this content may be harmful or inappropriate.",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = reportText,
                    onValueChange = { if (it.length <= 500) onReportTextChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = { Text("Describe the issue...") },
                    maxLines = 6
                )
                Text(
                    text = "${reportText.length}/500",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                enabled = reportText.trim().isNotEmpty()
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun submitReport(
    analysisId: String,
    reportText: String,
    result: CoughAnalysisResult
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val user = Firebase.auth.currentUser ?: return@launch
            val reportData = hashMapOf<String, Any>(
                "reportedBy" to user.uid,
                "reportedAt" to FieldValue.serverTimestamp(),
                "reason" to reportText.trim(),
                "analysisId" to analysisId,
                "analysisData" to hashMapOf(
                    "coughType" to result.coughType,
                    "severity" to result.severity,
                    "timestamp" to result.timestamp,
                    "confidence" to result.confidence
                ),
                "status" to "pending"
            )
            user.email?.let { reportData["reporterEmail"] = it }

            Firebase.firestore.collection("reports").add(reportData)
                .addOnSuccessListener {
                    Log.d("CoughAnalysisResult", "Report submitted successfully")
                    Firebase.analytics.logEvent("harmful_content_reported", Bundle().apply {
                        putString("analysis_id", analysisId)
                    })
                }
                .addOnFailureListener { e ->
                    Log.e("CoughAnalysisResult", "Error submitting report", e)
                }
        } catch (e: Exception) {
            Log.e("CoughAnalysisResult", "Error submitting report", e)
        }
    }
}

private fun formatTimestamp(timestamp: Double): String {
    val date = Date((timestamp * 1000).toLong())
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
}
