package com.twintipsolutions.cough

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.twintipsolutions.cough.ui.screens.LoadingScreen
import com.twintipsolutions.cough.ui.screens.ModernOnboardingView
import com.twintipsolutions.cough.ui.screens.ModernContentView
import com.twintipsolutions.cough.ui.theme.CoughTheme
import com.twintipsolutions.cough.domain.UserActivityManager
import com.twintipsolutions.cough.data.dataStore
import com.twintipsolutions.cough.data.ONBOARDING_COMPLETED_KEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics

class  MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var authStateChecked by mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("CoughApp", "=== FIREBASE INITIALIZATION ===")
        Log.d("CoughApp", "Firebase app name: ${FirebaseApp.getInstance().name}")
        Log.d("CoughApp", "Firebase app options: ${FirebaseApp.getInstance().options.applicationId}")
        Log.d("CoughApp", "Firebase project ID: ${FirebaseApp.getInstance().options.projectId}")
        
        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics
        
        // Log app open event
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        
        // Make app edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            CoughTheme {
                var hasCompletedOnboarding by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(true) }
                
                // Load onboarding state and check auth
                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        hasCompletedOnboarding = dataStore.data.first()[ONBOARDING_COMPLETED_KEY] ?: false
                        Log.d("CoughApp", "MainActivity: hasCompletedOnboarding = $hasCompletedOnboarding")
                        
                        // Check authentication state
                        val currentUser = Firebase.auth.currentUser
                        Log.d("CoughApp", "Current auth user: ${currentUser?.uid}")
                        Log.d("CoughApp", "Is anonymous: ${currentUser?.isAnonymous}")
                        
                        // If no authenticated user, reset onboarding
                        if (currentUser == null && hasCompletedOnboarding) {
                            Log.d("CoughApp", "MainActivity: No auth user, resetting onboarding")
                            dataStore.edit { settings ->
                                settings[ONBOARDING_COMPLETED_KEY] = false
                            }
                            hasCompletedOnboarding = false
                        }
                        
                        isLoading = false
                    }
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        isLoading -> {
                            // Show loading screen while Firebase initializes
                            LoadingScreen()
                        }
                        !hasCompletedOnboarding || Firebase.auth.currentUser == null -> {
                            // Show onboarding if not completed OR if user is not authenticated
                            Log.d("CoughApp", "MainActivity: Showing onboarding (completed: $hasCompletedOnboarding, auth: ${Firebase.auth.currentUser != null})")
                            ModernOnboardingView(
                                onComplete = {
                                    lifecycleScope.launch {
                                        dataStore.edit { settings ->
                                            settings[ONBOARDING_COMPLETED_KEY] = true
                                        }
                                        hasCompletedOnboarding = true
                                    }
                                }
                            )
                        }
                        else -> {
                            // User is authenticated and has completed onboarding
                            ModernContentView()
                        }
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // Update last active time only if authenticated
        if (Firebase.auth.currentUser != null) {
            UserActivityManager.updateLastActiveTime()
        }
        
        // Log screen view event
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        })
    }
}