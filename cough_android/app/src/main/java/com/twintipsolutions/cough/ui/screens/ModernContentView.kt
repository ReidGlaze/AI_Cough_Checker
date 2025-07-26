package com.twintipsolutions.cough.ui.screens

import android.Manifest
import com.twintipsolutions.cough.domain.model.CoughAnalysisResult
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.twintipsolutions.cough.domain.recorder.AudioRecorder
import com.twintipsolutions.cough.ui.components.AnimatedGradientBackground
import com.twintipsolutions.cough.ui.components.GlassmorphicCard
import com.twintipsolutions.cough.ui.components.PulseIcon
import com.twintipsolutions.cough.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.drawWithContent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import androidx.compose.foundation.layout.systemBarsPadding

@Composable
fun AnimatedMainGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "mainGradient")
    
    // Subtle animation like iOS
    val animateOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 15000, // 15 seconds for very smooth animation
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mainGradientAnimation"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                // Create a smooth moving gradient
                val gradientHeight = size.height * 1.5f
                val offset = (animateOffset - 0.25f) * gradientHeight * 0.3f
                
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1929), // Dark blue top
                            Color(0xFF1E3A5F), // Mid blue
                            Color(0xFF0D2438), // Dark teal
                            Color(0xFF1E3A5F), // Mid blue again for smooth loop
                            Color(0xFF0A1929)  // Dark blue bottom
                        ),
                        startY = offset,
                        endY = offset + gradientHeight
                    )
                )
                drawContent()
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ModernContentView() {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // Permission state
    val recordAudioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    // Collect state from AudioRecorder
    val isRecording by audioRecorder.isRecording.collectAsState()
    val isAnalyzing by audioRecorder.isAnalyzing.collectAsState()
    val recordingTimeMillis by audioRecorder.recordingTime.collectAsState()
    val recordingTime = recordingTimeMillis / 1000f
    val analysisResult by audioRecorder.analysisResult.collectAsState()
    val errorMessage by audioRecorder.errorMessage.collectAsState()
    
    var showResults by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showParticles by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var historicalResult by remember { mutableStateOf<CoughAnalysisResult?>(null) }
    
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing), // 3 seconds like iOS
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val waveformAnimation = rememberInfiniteTransition(label = "waveform")
    val waveformRotation by waveformAnimation.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing), // 2 seconds like iOS
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveformRotation"
    )
    
    val recordingScale by animateFloatAsState(
        targetValue = if (isRecording) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium // Matches iOS spring(response: 0.3, dampingFraction: 0.6)
        ),
        label = "recordingScale"
    )
    
    // Handle analysis result changes
    LaunchedEffect(analysisResult) {
        if (analysisResult != null) {
            showResults = true
        }
    }
    
    fun toggleRecording() {
        coroutineScope.launch {
            if (isRecording) {
                // Stop recording
                showParticles = false
                audioRecorder.stopRecording()
                
                // Log stop recording event
                Firebase.analytics.logEvent("recording_stopped", Bundle().apply {
                    putString("recording_duration", String.format("%.1f", recordingTime))
                })
            } else {
                // Check permission first
                if (recordAudioPermission.status == com.google.accompanist.permissions.PermissionStatus.Granted) {
                    // Start recording
                    showParticles = true
                    audioRecorder.startRecording()
                    
                    // Log start recording event
                    Firebase.analytics.logEvent("recording_started", null)
                } else {
                    // Request permission
                    if (recordAudioPermission.status is com.google.accompanist.permissions.PermissionStatus.Denied && 
                        (recordAudioPermission.status as com.google.accompanist.permissions.PermissionStatus.Denied).shouldShowRationale) {
                        showPermissionDialog = true
                    } else {
                        recordAudioPermission.launchPermissionRequest()
                    }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // iOS-style animated dark blue/teal gradient background
        AnimatedMainGradientBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Add system bars padding
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section with pulse icon in rounded square
            Column(
                modifier = Modifier.padding(top = 60.dp), // Reduced top padding since we're using systemBarsPadding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulse icon in rounded square like iOS
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E88E5)) // Bright blue like iOS
                        .border(
                            width = 2.dp,
                            color = Color(0xFF42A5F5), // Lighter blue border
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PulseIcon(
                        modifier = Modifier.size(50.dp),
                        color = Color.White,
                        strokeWidth = 3f
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = "Cough Checker",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Main recording button
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (showParticles && isRecording) {
                    RecordingParticlesView()
                }
                
                Button(
                    onClick = { toggleRecording() },
                    modifier = Modifier
                        .size(250.dp)
                        .scale(recordingScale),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // iOS-style gray circle background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF37474F)) // Gray like iOS
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFF455A64), // Slightly lighter gray border
                                    shape = CircleShape
                                )
                        )
                        
                        // Animated circles when recording
                        if (isRecording) {
                            repeat(3) { index ->
                                val infiniteTransition = rememberInfiniteTransition(label = "circle$index")
                                val scale by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1f + index * 0.3f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 1500, // 1.5 seconds like iOS
                                            easing = FastOutLinearInEasing,
                                            delayMillis = index * 300
                                        ),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "circleScale$index"
                                )
                                val alpha by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 1500,
                                            easing = FastOutLinearInEasing,
                                            delayMillis = index * 300
                                        ),
                                        repeatMode = RepeatMode.Restart
                                    ),
                                    label = "circleAlpha$index"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .scale(scale)
                                        .graphicsLayer { this.alpha = alpha }
                                        .border(
                                            width = 2.dp,
                                            color = CoughColors.RedOpacity30,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                        
                        // Button content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when {
                                isAnalyzing -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(60.dp),
                                        color = Color.White,
                                        strokeWidth = 4.dp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(15.dp))
                                    
                                    Text(
                                        text = "Analyzing...",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                else -> {
                                    // Inner circle with mic icon like iOS
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00BCD4)), // Cyan like iOS
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .scale(
                                                    animateFloatAsState(
                                                        targetValue = if (isRecording) 1.1f else 1f,
                                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                                        label = "iconScale"
                                                    ).value
                                                ),
                                            tint = Color.White
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    Text(
                                        text = if (isRecording) {
                                            "Recording... ${String.format("%.1f", recordingTime)}s"
                                        } else {
                                            "Tap to Analyze"
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom navigation - iOS style
            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp), // Reduced padding since we're using systemBarsPadding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { 
                        showHistory = true
                        Firebase.analytics.logEvent("history_opened", null)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp) // Add vertical padding
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF37474F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(35.dp),
                                tint = Color(0xFF00BCD4) // Cyan like iOS
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "History & Info",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 4.dp) // Increased horizontal padding to prevent cutoff
                        )
                    }
                }
            }
        }
    }
    
    // Show results
    if (showResults && (analysisResult != null || historicalResult != null)) {
        CoughAnalysisResultView(
            result = historicalResult ?: analysisResult!!,
            onClose = {
                showResults = false
                historicalResult = null
            }
        )
    }
    
    // Show permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Microphone Permission Required") },
            text = { Text("This app needs access to your microphone to record and analyze your cough. Please grant the permission to continue.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        recordAudioPermission.launchPermissionRequest()
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Show error message
    errorMessage?.let { error ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(
                    onClick = { /* Clear error */ }
                ) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(error)
        }
    }
    
    // Show history sheet
    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            containerColor = Color(0xFF1C2951),
            contentColor = Color.White,
            modifier = Modifier.fillMaxHeight(1f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            HistoryInfoView(
                onDismiss = { showHistory = false },
                onAnalysisClick = { analysis ->
                    historicalResult = analysis
                    showResults = true
                    showHistory = false
                }
            )
        }
    }
}

@Composable
fun NavigationButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(60.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CoughColors.WhiteOpacity10)
                .blur(radius = 10.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = GlassmorphicBorderColors,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RecordingParticlesView() {
    // Simple particle effect
    Box(modifier = Modifier.size(400.dp)) {
        repeat(5) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "particle$index")
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "particleY$index"
            )
            
            Box(
                modifier = Modifier
                    .offset(x = (index * 40).dp, y = offsetY.dp)
                    .size(20.dp)
                    .background(
                        color = CoughColors.RedOpacity30,
                        shape = CircleShape
                    )
            )
        }
    }
}

