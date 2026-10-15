package com.homefit

import com.homefit.app.currentLocalWeekday
import java.time.LocalDate
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalWeekdayPlatformTest {
    @Test fun `today follows the local timezone`() {
        val original = TimeZone.getDefault()
        try {
            for (zone in listOf("UTC", "Asia/Jakarta", "Pacific/Honolulu")) {
                TimeZone.setDefault(TimeZone.getTimeZone(zone))
                assertEquals(LocalDate.now().dayOfWeek.value, currentLocalWeekday(), zone)
            }
        } finally {
            TimeZone.setDefault(original)
        }
    }
}
