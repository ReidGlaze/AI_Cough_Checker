package com.twintipsolutions.cough.domain.model

/**
 * Converts a saved analysis `timestamp` into epoch milliseconds for display.
 *
 * The backend saves `timestamp` as milliseconds since 1970 (JavaScript
 * `Date.now()`), while the app's own fallback values are seconds. Reading a
 * millisecond value as seconds put dates in the year 57,000+. Every analysis
 * already saved keeps its stored value; only the reading of it changes here.
 */
object CoughTimestamp {
    /**
     * Values at or above this cannot be seconds (as seconds it is the year
     * 5138), so they are milliseconds (as milliseconds it is March 1973).
     */
    const val MILLISECONDS_THRESHOLD = 100_000_000_000.0

    fun toEpochMillis(stored: Double): Long =
        if (stored >= MILLISECONDS_THRESHOLD) stored.toLong() else (stored * 1000).toLong()
}
