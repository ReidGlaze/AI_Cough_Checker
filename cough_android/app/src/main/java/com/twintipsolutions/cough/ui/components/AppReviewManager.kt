package com.twintipsolutions.cough.ui.components

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.concurrent.TimeUnit

object AppReviewManager {
    private const val TAG = "AppReviewManager"
    private const val PREFS_NAME = "app_review_prefs"

    // Keys for SharedPreferences
    private const val ANALYSIS_COUNT_KEY = "analysis_count"
    private const val HISTORY_VIEW_COUNT_KEY = "history_view_count"
    private const val LAST_REVIEW_REQUEST_KEY = "last_review_request"
    private const val REVIEW_REQUEST_COUNT_KEY = "review_request_count"

    // Thresholds for requesting review
    private const val ANALYSIS_THRESHOLD = 2  // Request after 2 analyses
    private const val HISTORY_VIEW_THRESHOLD = 3  // Or after viewing history 3 times
    private const val MINIMUM_DAYS_BETWEEN_REQUESTS = 14L  // Don't ask more than once every 14 days

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Call this after each successful cough analysis
     */
    fun incrementAnalysisCount(context: Context) {
        val prefs = getPrefs(context)
        val currentCount = prefs.getInt(ANALYSIS_COUNT_KEY, 0)
        val newCount = currentCount + 1
        prefs.edit().putInt(ANALYSIS_COUNT_KEY, newCount).apply()

        Log.d(TAG, "Analysis count: $newCount")

        // Check if we should request a review
        if (newCount >= ANALYSIS_THRESHOLD && newCount % ANALYSIS_THRESHOLD == 0) {
            requestReviewIfAppropriate(context)
        }
    }

    /**
     * Call this when user views their history
     */
    fun incrementHistoryViewCount(context: Context) {
        val prefs = getPrefs(context)
        val currentCount = prefs.getInt(HISTORY_VIEW_COUNT_KEY, 0)
        val newCount = currentCount + 1
        prefs.edit().putInt(HISTORY_VIEW_COUNT_KEY, newCount).apply()

        Log.d(TAG, "History view count: $newCount")

        // Check if we should request a review (engaged users who check history)
        if (newCount >= HISTORY_VIEW_THRESHOLD && newCount % HISTORY_VIEW_THRESHOLD == 0) {
            requestReviewIfAppropriate(context)
        }
    }

    /**
     * Manual request from settings/about screen
     */
    fun requestReviewManually(activity: Activity) {
        launchReviewFlow(activity)
    }

    private fun requestReviewIfAppropriate(context: Context) {
        val prefs = getPrefs(context)

        // Check if enough time has passed since last request
        val lastRequestTime = prefs.getLong(LAST_REVIEW_REQUEST_KEY, 0)
        if (lastRequestTime > 0) {
            val daysSinceLastRequest = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - lastRequestTime
            )

            if (daysSinceLastRequest < MINIMUM_DAYS_BETWEEN_REQUESTS) {
                Log.d(TAG, "Skipping review request - only $daysSinceLastRequest days since last request")
                return
            }
        }

        // Check total analysis count - only ask users who have used the app meaningfully
        val totalAnalyses = prefs.getInt(ANALYSIS_COUNT_KEY, 0)
        if (totalAnalyses < ANALYSIS_THRESHOLD) {
            Log.d(TAG, "Skipping review request - not enough analyses ($totalAnalyses)")
            return
        }

        // Try to get the activity from context
        if (context is Activity) {
            launchReviewFlow(context)
        } else {
            Log.d(TAG, "Context is not an Activity, cannot show review dialog")
        }
    }

    private fun launchReviewFlow(activity: Activity) {
        val prefs = getPrefs(activity)

        // Update tracking
        prefs.edit()
            .putLong(LAST_REVIEW_REQUEST_KEY, System.currentTimeMillis())
            .putInt(REVIEW_REQUEST_COUNT_KEY, prefs.getInt(REVIEW_REQUEST_COUNT_KEY, 0) + 1)
            .apply()

        Log.d(TAG, "Launching review flow")

        val reviewManager = ReviewManagerFactory.create(activity)
        val requestFlow = reviewManager.requestReviewFlow()

        requestFlow.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d(TAG, "Review flow completed")
                }
            } else {
                Log.e(TAG, "Failed to request review flow", task.exception)
            }
        }
    }

    /**
     * Reset all counters (useful for testing)
     */
    fun resetAllCounters(context: Context) {
        getPrefs(context).edit().clear().apply()
        Log.d(TAG, "All counters reset")
    }

    /**
     * Get current stats (for debugging)
     */
    fun getDebugStats(context: Context): String {
        val prefs = getPrefs(context)
        val analyses = prefs.getInt(ANALYSIS_COUNT_KEY, 0)
        val historyViews = prefs.getInt(HISTORY_VIEW_COUNT_KEY, 0)
        val requests = prefs.getInt(REVIEW_REQUEST_COUNT_KEY, 0)
        val lastRequest = prefs.getLong(LAST_REVIEW_REQUEST_KEY, 0)

        return """
            Analyses: $analyses
            History Views: $historyViews
            Review Requests: $requests
            Last Request: ${if (lastRequest > 0) java.util.Date(lastRequest) else "Never"}
        """.trimIndent()
    }
}
