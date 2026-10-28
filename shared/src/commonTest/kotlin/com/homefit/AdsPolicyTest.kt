package com.homefit

import com.homefit.feature.app.AppState
import com.homefit.feature.app.interstitialEligible
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdsPolicyTest {
    @Test fun `first completed workout never gets interstitial`() {
        assertFalse(interstitialEligible(AppState(completedWorkoutIds = setOf("one")), 86_400_000L))
    }

    @Test fun `later workout is capped for rolling 24 hours and clock rollback is safe`() {
        val state = AppState(completedWorkoutIds = setOf("one", "two"), lastInterstitialShownEpochMs = 100L)
        assertFalse(interstitialEligible(state, 100L + 86_399_999L))
        assertTrue(interstitialEligible(state, 100L + 86_400_000L))
        assertFalse(interstitialEligible(state, 99L))
    }
}
