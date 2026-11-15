package com.twintipsolutions.cough.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CoughTimestampTest {
    private fun year(epochMillis: Long): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = Date(epochMillis)
        return calendar.get(Calendar.YEAR)
    }

    @Test
    fun millisecondsFromBackendGiveTheRealDate() {
        // 2025-09-17T00:00:00Z as the backend stores it (Date.now()).
        val millis = CoughTimestamp.toEpochMillis(1_758_067_200_000.0)
        assertEquals(1_758_067_200_000L, millis)
        assertEquals(2025, year(millis))
    }

    @Test
    fun oldBugWouldHaveShownYear57000() {
        val wrong = (1_758_067_200_000.0 * 1000).toLong()
        assertTrue(year(wrong) > 57_000)
    }

    @Test
    fun secondsStayAsTheyAre() {
        val millis = CoughTimestamp.toEpochMillis(1_758_067_200.0)
        assertEquals(1_758_067_200_000L, millis)
        assertEquals(2025, year(millis))
    }

    @Test
    fun fractionalSecondsKeepTheirMilliseconds() {
        assertEquals(1_758_067_200_250L, CoughTimestamp.toEpochMillis(1_758_067_200.25))
    }

    @Test
    fun oldestSavedHistoryInMilliseconds() {
        // 2025-07-01T00:00:00Z, before the app's first release.
        assertEquals(2025, year(CoughTimestamp.toEpochMillis(1_751_328_000_000.0)))
    }

    @Test
    fun zeroFallbackIsUnchanged() {
        assertEquals(0L, CoughTimestamp.toEpochMillis(0.0))
    }
}
