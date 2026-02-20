package com.twintipsolutions.cough.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object UserActivityManager {
    private const val TAG = "UserActivityManager"
    private const val UPDATE_INTERVAL_MINUTES = 5L
    private var lastUpdateTime: Long = 0

    fun updateLastActiveTime() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime < TimeUnit.MINUTES.toMillis(UPDATE_INTERVAL_MINUTES)) {
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = currentUser.uid

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(uid)
                userRef.update("lastActiveAt", com.google.firebase.Timestamp.now())
                lastUpdateTime = currentTime
            } catch (e: Exception) {
                // Silently fail - this is not critical
            }
        }
    }

    fun updateOnSignIn() {
        lastUpdateTime = 0L
        updateLastActiveTime()
    }
}
