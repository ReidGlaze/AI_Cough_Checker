package com.twintipsolutions.cough.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twintipsolutions.cough.ui.components.AnimatedGradientBackground
import com.twintipsolutions.cough.ui.components.GlassmorphicCard
import com.twintipsolutions.cough.ui.components.PulseIcon
import com.twintipsolutions.cough.ui.theme.*
import com.twintipsolutions.cough.domain.UserActivityManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import kotlin.random.Random

// Helper function to create or update user document
private suspend fun createOrUpdateUserDocument(uid: String) {
    try {
        Log.d("CoughApp", "📝 Creating/updating user document in Firestore...")
        
        // Get timezone
        val timezone = java.util.TimeZone.getDefault().id
        
        val userData = hashMapOf(
            "lastActiveAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "platform" to "Android", // Capitalized like iOS
            "appVersion" to "1.0", // Match iOS format
            "timezone" to timezone
        )
        
        // Check if document exists
        val docRef = Firebase.firestore.collection("users").document(uid)
        val document = docRef.get().await()
        
        if (!document.exists()) {
            // New user - add createdAt and set uid
            userData["createdAt"] = com.google.firebase.firestore.FieldValue.serverTimestamp()
            userData["uid"] = uid
            userData["totalAnalyses"] = 0
        }
        
        // Set or update the document
        docRef.set(userData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Log.d("CoughApp", "✅ User document ${if(document.exists()) "updated" else "created"} successfully!")
                Log.d("CoughApp", "Document ID: $uid")
                Log.d("CoughApp", "Data written: $userData")
            }
            .addOnFailureListener { e ->
                Log.e("CoughApp", "❌ Failed to create/update user document", e)
            }
            .await()
    } catch (e: Exception) {
        Log.e("CoughApp", "❌ Firestore error: ${e.message}", e)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModernOnboardingView(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    var hasStartedAuth by remember { mutableStateOf(false) }
    
    Log.d("CoughApp", "ModernOnboardingView: Current page = ${pagerState.currentPage}")
    
    // Handle authentication once when onboarding starts
    LaunchedEffect(Unit) {
        if (!hasStartedAuth && Firebase.auth.currentUser == null) {
            hasStartedAuth = true
            Log.d("CoughApp", "🚀 Starting authentication in onboarding...")
            coroutineScope.launch {
                delay(500) // Small delay to let UI settle
                try {
                    Log.d("CoughApp", "🔑 Signing in anonymously...")
                    val result = Firebase.auth.signInAnonymously().await()
                    Log.d("CoughApp", "✅ Sign in successful: ${result.user?.uid}")
                    result.user?.let { user ->
                        createOrUpdateUserDocument(user.uid)
                        UserActivityManager.updateOnSignIn()
                    }
                } catch (e: Exception) {
                    Log.e("CoughApp", "❌ Authentication error: ${e.message}", e)
                }
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Animated gradient background with particles
        AnimatedGradientBackgroundWithParticles()
        
        // Pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ModernWelcomeView(
                    isCurrentPage = pagerState.currentPage == 0,
                    onNext = { 
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                1 -> ModernAboutView()
                2 -> ModernHowItWorksView()
                3 -> ModernPrivacyView()
                4 -> ModernGetStartedView(onComplete = onComplete)
            }
        }
        
        // Custom progress indicator with padding for navigation bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Increased padding to avoid nav bar overlap
            contentAlignment = Alignment.BottomCenter
        ) {
            CustomProgressIndicator(
                currentPage = pagerState.currentPage,
                totalPages = 5
            )
        }
    }
}

@Composable
fun AnimatedGradientBackgroundWithParticles() {
    // Simply use the gradient background without particles
    AnimatedGradientBackground()
}

@Composable
fun ModernWelcomeView(
    isCurrentPage: Boolean,
    onNext: () -> Unit
) {
    var showElements by remember { mutableStateOf(false) }
    
    Log.d("CoughApp", "ModernWelcomeView composing... isCurrentPage: $isCurrentPage, Current user: ${Firebase.auth.currentUser?.uid}")
    
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), // Slower and smoother
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            Log.d("CoughApp", "ModernWelcomeView LaunchedEffect started - page is current")
            showElements = true
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Animated logo with glow
        Box {
            // Glow effect - simplified
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CoughColors.PurpleOpacity30,
                                Color.Transparent
                            ),
                            radius = 80f
                        ),
                        shape = CircleShape
                    )
            )
            
            // Pulse Icon
            PulseIcon(
                modifier = Modifier
                    .size(80.dp)
                    .scale(
                        animateFloatAsState(
                            targetValue = if (showElements) 1f else 0.5f,
                            animationSpec = tween(1000, easing = FastOutSlowInEasing),
                            label = "iconScale"
                        ).value
                    )
                    .graphicsLayer {
                        alpha = if (showElements) 1f else 0f
                    },
                color = Color.White,
                strokeWidth = 4f
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Title with gradient
        AnimatedVisibility(
            visible = showElements,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Cough Checker",
                    fontSize = 36.sp, // Reduced from 48sp to prevent overlap
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    letterSpacing = 0.5.sp // Add letter spacing
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "AI-Powered Health Insights",
                    fontSize = 20.sp,
                    color = CoughColors.WhiteOpacity80,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Swipe instruction
        AnimatedVisibility(
            visible = showElements,
            enter = fadeIn() + slideInVertically { it }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 100.dp)
            ) {
                Text(
                    text = "Swipe to continue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = CoughColors.WhiteOpacity70
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = CoughColors.WhiteOpacity50,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = CoughColors.WhiteOpacity70
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp)) // More space for nav bar
    }
}

@Composable
fun ModernAboutView() {
    var showFeatures by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showFeatures = true
    }
    
    val features = listOf(
        Triple(Icons.Default.Mic, "Advanced Audio Recording", "Capture high-quality cough samples"),
        Triple(Icons.Default.SmartToy, "AI Analysis", "Powered by cutting-edge machine learning"),
        Triple(Icons.Default.BarChart, "Pattern Tracking", "Monitor your health trends over time"),
        Triple(Icons.Default.Description, "Detailed Insights", "Educational health information and patterns")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Discover Features",
            fontSize = 34.sp, // Smaller to match iOS
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            features.forEachIndexed { index, feature ->
                AnimatedVisibility(
                    visible = showFeatures,
                    enter = fadeIn() + slideInHorizontally { -it },
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    FeatureCard(
                        icon = feature.first,
                        title = feature.second,
                        description = feature.third,
                        animationDelay = index * 100
                    )
                }
                
                if (index < features.size - 1) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp)) // More space for nav bar
    }
}

@Composable
fun ModernHowItWorksView() {
    var currentStep by remember { mutableStateOf(0) }
    
    val steps = listOf(
        StepData("1", "Record", "Tap and cough naturally", Icons.Default.Mic, CoughColors.Blue),
        StepData("2", "Analyze", "AI processes your audio", Icons.Default.GraphicEq, CoughColors.Purple),
        StepData("3", "Results", "View detailed insights", Icons.Default.BarChart, CoughColors.Green)
    )
    
    LaunchedEffect(Unit) {
        // Ensure we start at step 0
        currentStep = 0
        // Give UI time to settle with step 0
        delay(500)
        
        // Start rotation every 2.5 seconds like iOS
        while (true) {
            delay(2500)
            currentStep = (currentStep + 1) % steps.count()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        
        Text(
            text = "How It Works",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 60.dp)
        )
        
        Box(
            modifier = Modifier.size(width = 300.dp, height = 400.dp),
            contentAlignment = Alignment.Center
        ) {
            // Show only the current step card
            StepCard(
                step = steps[currentStep],
                isActive = true,
                offset = 0
            )
        }
        
        Spacer(modifier = Modifier.weight(0.5f))
        
        Spacer(modifier = Modifier.height(120.dp)) // More space for nav bar
    }
}

@Composable
fun ModernPrivacyView() {
    var showItems by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showItems = true
    }
    
    val privacyFeatures = listOf(
        Triple(Icons.Default.Lock, "Secure Cloud Processing", CoughColors.Purple),
        Triple(Icons.Default.PersonOff, "Anonymous by Default", CoughColors.Blue),
        Triple(Icons.Default.SmartToy, "AI Doesn't Train on Your Data", CoughColors.Green),
        Triple(Icons.Default.Delete, "Delete Anytime", CoughColors.Red)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        
        // Icon with gradient background like iOS
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Gradient background with glow
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .blur(radius = 15.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CoughColors.PurpleOpacity50,
                                Color.Transparent
                            ),
                            radius = 60f
                        ),
                        shape = CircleShape
                    )
            )
            
            // Shield with lock
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF9C27B0).copy(alpha = 0.8f), // Purple
                                Color(0xFF673AB7).copy(alpha = 0.8f)  // Deeper purple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(45.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(50.dp))
        
        Text(
            text = "Your Privacy First",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(50.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            privacyFeatures.forEachIndexed { index, feature ->
                PrivacyFeatureRow(
                    icon = feature.first,
                    text = feature.second,
                    color = feature.third,
                    animationDelay = if (showItems) index * 100 else 0
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(0.5f))
        
        Spacer(modifier = Modifier.height(120.dp)) // More space for nav bar
    }
}

@Composable
fun ModernGetStartedView(onComplete: () -> Unit) {
    var showContent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        showContent = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        
        // Success icon with sparkles
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Sparkles around the icon
            if (showContent) {
                SmallSparklesView()
            }
            
            // Badge-style checkmark
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(
                        animateFloatAsState(
                            targetValue = if (showContent) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = 0.6f,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "iconScale"
                        ).value
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 40.dp,
                            topEnd = 40.dp,
                            bottomEnd = 40.dp,
                            bottomStart = 40.dp
                        )
                    )
                    .background(CoughColors.Green),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(50.dp))
        
        Text(
            text = "Ready to Begin!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Your journey to better health awareness\nstarts now",
            fontSize = 18.sp,
            color = CoughColors.WhiteOpacity80,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.weight(0.2f))
        
        // Button
        Button(
            onClick = {
                Log.d("CoughApp", "Start Using Cough Checker clicked")
                Log.d("CoughApp", "Current user: ${Firebase.auth.currentUser?.uid}")
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CoughColors.WhiteOpacity10)
                    .border(
                        width = 1.dp,
                        color = CoughColors.WhiteOpacity30,
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Using Cough Checker",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(0.3f))
        
        Spacer(modifier = Modifier.height(120.dp)) // Space for nav bar
    }
}

// Supporting components
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    animationDelay: Int
) {
    LaunchedEffect(animationDelay) {
        delay(animationDelay.toLong())
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CoughColors.WhiteOpacity05)
            .border(
                width = 1.dp,
                color = CoughColors.WhiteOpacity30,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CoughColors.PurpleOpacity30),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = CoughColors.WhiteOpacity60,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
            }
        }
    }
}

data class StepData(
    val number: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun StepCard(
    step: StepData,
    isActive: Boolean,
    offset: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CoughColors.WhiteOpacity10)
            .border(
                width = 1.dp,
                color = CoughColors.WhiteOpacity30,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = step.color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = step.color
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "Step ${step.number}",
                fontSize = 14.sp,
                color = CoughColors.WhiteOpacity70
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = step.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = step.description,
                fontSize = 17.sp,
                color = CoughColors.WhiteOpacity80,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PrivacyFeatureRow(
    icon: ImageVector,
    text: String,
    color: Color,
    animationDelay: Int
) {
    LaunchedEffect(animationDelay) {
        delay(animationDelay.toLong())
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.width(20.dp))
        
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun CustomProgressIndicator(
    currentPage: Int,
    totalPages: Int
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(CoughColors.WhiteOpacity10)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalPages) { page ->
                val isActive = currentPage == page
                Box(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring()
                        )
                        .width(if (isActive) 30.dp else 10.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isActive) Color.White else CoughColors.WhiteOpacity30
                        )
                )
            }
        }
    }
}

@Composable
fun ParticleEffectView() {
    // Simple particle effect without state modification issues
    Box(modifier = Modifier.fillMaxSize()) {
        // Just show a few static particles for visual effect
        repeat(8) { index ->
            val offsetX = remember { Random.nextFloat() }
            val offsetY = remember { Random.nextFloat() }
            val scale = remember { Random.nextFloat() * 0.5f + 0.5f }
            
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        translationX = offsetX * size.width
                        translationY = offsetY * size.height
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.3f
                    },
                tint = Color.White
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val scale: Float,
    val opacity: Float
)

@Composable
fun SmallSparklesView() {
    val sparklePositions = remember {
        listOf(
            Offset(-60f, -60f),
            Offset(60f, -60f),
            Offset(-80f, 0f),
            Offset(80f, 0f),
            Offset(-60f, 60f),
            Offset(60f, 60f),
            Offset(0f, -80f),
            Offset(0f, 80f)
        )
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )
    
    sparklePositions.forEach { position ->
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .offset(position.x.dp, position.y.dp)
                .graphicsLayer {
                    alpha = sparkleAlpha
                },
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}