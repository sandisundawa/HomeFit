package com.homefit

import com.homefit.app.isoWeekday
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalWeekdayTest {
    @Test fun `calendar weekdays match Monday-first schedule including Sunday`() {
        assertEquals(listOf(7, 1, 2, 3, 4, 5, 6), (1..7).map(::isoWeekday))
    }
}
