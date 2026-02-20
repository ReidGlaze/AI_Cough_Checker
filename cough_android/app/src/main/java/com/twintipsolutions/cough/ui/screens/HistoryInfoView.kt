package com.twintipsolutions.cough.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.twintipsolutions.cough.data.ONBOARDING_COMPLETED_KEY
import com.twintipsolutions.cough.data.dataStore
import com.twintipsolutions.cough.domain.model.CoughAnalysisResult
import com.twintipsolutions.cough.domain.model.PotentialCause
import com.twintipsolutions.cough.ui.components.AppReviewManager
import com.twintipsolutions.cough.ui.components.GlassmorphicCard
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryInfoView(
    onDismiss: () -> Unit,
    onAnalysisClick: (CoughAnalysisResult) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Title
        Text(
            text = if (selectedTab == 0) "History" else "Information",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 20.dp),
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        // Tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
        ) {
            TabButton(
                title = "History",
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )
            TabButton(
                title = "Info",
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
        }

        // Content
        if (selectedTab == 0) {
            HistoryTab(onAnalysisClick = onAnalysisClick)
        } else {
            InfoTab(onDeleteAccountClick = { showDeleteAccountDialog = true })
        }
    }

    // Delete account confirmation dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text("This will permanently delete your account and all analysis history. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        coroutineScope.launch {
                            deleteAccount(context)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(vertical = 8.dp),
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (isSelected) Color.White else Color.Transparent)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTab(onAnalysisClick: (CoughAnalysisResult) -> Unit) {
    var analyses by remember { mutableStateOf<List<CoughAnalysisResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = Firebase.functions
                .getHttpsCallable("getAnalysisHistory")
                .call(hashMapOf("limit" to 50))
                .await()

            val data = result.data as? Map<*, *>
            val history = data?.get("history") as? List<*>

            analyses = history?.mapNotNull { item ->
                val map = item as? Map<*, *> ?: return@mapNotNull null
                val results = map["results"] as? Map<*, *> ?: return@mapNotNull null
                val insights = map["insights"] as? Map<*, *> ?: return@mapNotNull null

                val coughType = results["coughType"] as? String ?: "unknown"
                val severity = results["severity"] as? String ?: "mild"
                val normalizedSeverity = if (severity == "none") "mild" else severity
                val urgency = results["urgency"] as? String ?: "routine"
                val normalizedUrgency = if (urgency == "none") "routine" else urgency

                val potentialCauses = (results["potentialCauses"] as? List<*>)?.mapNotNull { cause ->
                    val causeMap = cause as? Map<*, *> ?: return@mapNotNull null
                    PotentialCause(
                        condition = causeMap["condition"] as? String ?: return@mapNotNull null,
                        likelihood = causeMap["likelihood"] as? String ?: return@mapNotNull null,
                        description = causeMap["description"] as? String ?: return@mapNotNull null
                    )
                } ?: emptyList()

                CoughAnalysisResult(
                    analysisId = map["id"] as? String ?: map["analysisId"] as? String ?: "",
                    timestamp = (map["timestamp"] as? Number)?.toDouble() ?: 0.0,
                    coughType = coughType,
                    severity = normalizedSeverity,
                    characteristics = (results["characteristics"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    potentialCauses = potentialCauses,
                    managementApproaches = (results["managementApproaches"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    urgency = normalizedUrgency,
                    confidence = (results["confidence"] as? Number)?.toDouble() ?: 0.5,
                    soundPattern = insights["soundPattern"] as? String ?: "",
                    frequency = insights["frequency"] as? String ?: "",
                    duration = insights["duration"] as? String ?: "",
                    additionalNotes = (insights["additionalNotes"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("HistoryTab", "Error fetching history", e)
        } finally {
            isLoading = false
        }
    }

    // Increment history view count for review prompt
    LaunchedEffect(Unit) {
        AppReviewManager.incrementHistoryViewCount(context)
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        analyses.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No Analysis History",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your cough analyses will appear here",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = analyses,
                    key = { it.id }
                ) { analysis ->
                    SwipeableHistoryItem(
                        analysis = analysis,
                        onClick = {
                            Firebase.analytics.logEvent("history_item_clicked", Bundle().apply {
                                putString("cough_type", analysis.coughType)
                                putString("severity", analysis.severity)
                            })
                            onAnalysisClick(analysis)
                        },
                        onDelete = {
                            val removed = analysis
                            analyses = analyses.filter { it.id != analysis.id }
                            coroutineScope.launch {
                                try {
                                    val userId = Firebase.auth.currentUser?.uid ?: return@launch
                                    val db = Firebase.firestore
                                    db.collection("users")
                                        .document(userId)
                                        .collection("analyses")
                                        .document(analysis.analysisId)
                                        .delete()
                                        .await()
                                    db.collection("users")
                                        .document(userId)
                                        .update("totalAnalyses", FieldValue.increment(-1))
                                        .await()
                                } catch (e: Exception) {
                                    analyses = (analyses + removed).sortedByDescending { it.timestamp }
                                    Log.e("HistoryTab", "Error deleting analysis", e)
                                }
                            }
                        }
                    )
                }

                item {
                    Text(
                        text = "${analyses.size} analyses recorded",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableHistoryItem(
    analysis: CoughAnalysisResult,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            HistoryItem(analysis = analysis, onClick = onClick)
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}

@Composable
private fun HistoryItem(
    analysis: CoughAnalysisResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Severity icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (analysis.severity) {
                            "severe" -> Color.Red.copy(alpha = 0.2f)
                            "moderate" -> Color(0xFFFFA500).copy(alpha = 0.2f)
                            else -> Color.Green.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = when (analysis.severity) {
                        "severe" -> Color.Red
                        "moderate" -> Color(0xFFFFA500)
                        else -> Color.Green
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(analysis.timestamp),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = analysis.coughType,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = analysis.severityColor
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = analysis.severity,
                        color = analysis.severityColor,
                        fontSize = 13.sp
                    )
                }
                if (analysis.duration.isNotEmpty()) {
                    Text(
                        text = analysis.duration,
                        modifier = Modifier.padding(top = 2.dp),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            // Confidence
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(analysis.confidence * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "confidence",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View details",
                modifier = Modifier.size(20.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun InfoTab(onDeleteAccountClick: () -> Unit) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Important Notes
        item {
            Column {
                Text(
                    text = "Important Notes",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    InfoRow(icon = Icons.Default.Person, text = "For human coughs only")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(icon = Icons.Default.Info, text = "Educational purposes - not medical advice")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(icon = Icons.Default.GraphicEq, text = "Record in quiet environment for best results")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(icon = Icons.Default.VolumeOff, text = "Background noise affects accuracy")
                }
            }
        }

        // Account Section
        item {
            Column {
                Text(
                    text = "Account",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    // User email
                    Firebase.auth.currentUser?.email?.let { email ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = email,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Delete Account
                    InfoItem(
                        icon = Icons.Default.Delete,
                        text = "Delete Account",
                        onClick = onDeleteAccountClick
                    )
                }
            }
        }

        // Support Section
        item {
            Column {
                Text(
                    text = "Support",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(
                        icon = Icons.Default.Star,
                        text = "Rate Cough Checker",
                        onClick = {
                            (context as? Activity)?.let {
                                AppReviewManager.requestReviewManually(it)
                            }
                        }
                    )
                }
            }
        }

        // Legal Section
        item {
            Column {
                Text(
                    text = "Legal",
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(
                        icon = Icons.Default.Lock,
                        text = "Privacy Policy",
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://aicoughchecker.vercel.app/privacy"))
                            )
                        }
                    )
                }
            }
        }

        // Version
        item {
            Text(
                text = "Version ${getAppVersion(context)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp
        )
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}

private suspend fun deleteAccount(context: android.content.Context) {
    val user = Firebase.auth.currentUser ?: return
    val db = Firebase.firestore

    try {
        // Delete all analyses
        val analyses = db.collection("users")
            .document(user.uid)
            .collection("analyses")
            .get()
            .await()

        for (doc in analyses.documents) {
            doc.reference.delete().await()
        }

        // Delete user document
        db.collection("users").document(user.uid).delete().await()

        // Delete auth account
        user.delete().await()

        // Reset onboarding
        context.dataStore.edit { settings ->
            settings[ONBOARDING_COMPLETED_KEY] = false
        }
    } catch (e: Exception) {
        Log.e("HistoryInfoView", "Error deleting account", e)
    }
}

private fun formatDate(timestamp: Double): String {
    val date = Date((timestamp * 1000).toLong())
    return SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(date)
}

private fun getAppVersion(context: android.content.Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}
