package com.homefit

import com.homefit.feature.app.CountdownTick
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

@OptIn(ExperimentalTime::class)
class CountdownTickTest {
    @Test fun pausePreservesPartialTickAndIgnoresTimeAway() {
        val clock = TestTimeSource()
        val tick = CountdownTick(clock)
        tick.start()
        clock += 250.milliseconds
        tick.pause()
        assertEquals(750.milliseconds, tick.remaining)
        clock += 30.seconds
        tick.pause()
        assertEquals(750.milliseconds, tick.remaining)
        tick.start()
        clock += 250.milliseconds
        tick.pause()
        assertEquals(500.milliseconds, tick.remaining)
        tick.reset()
        assertEquals(1.seconds, tick.remaining)
    }

    @Test fun overdueTickNeverHasNegativeDelay() {
        val clock = TestTimeSource()
        val tick = CountdownTick(clock)
        tick.start()
        clock += 2.seconds
        tick.pause()
        assertEquals(Duration.ZERO, tick.remaining)
    }
}
