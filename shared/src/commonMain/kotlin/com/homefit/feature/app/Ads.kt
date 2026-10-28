package com.homefit.feature.app

import kotlin.time.Clock

/** Platform boundary for ads. Android owns SDK objects; other targets safely no-op. */
interface AdsHost {
    val supportsRewarded: Boolean
    val supportsPrivacyOptions: Boolean
    fun showSummaryInterstitial(state: AppState, onShown: () -> Unit, onFinished: () -> Unit)
    fun showBonusRewarded(onRewarded: () -> Unit, onFinished: () -> Unit)
    fun showPrivacyOptions(onFinished: () -> Unit)
}

object NoOpAdsHost : AdsHost {
    override val supportsRewarded = false
    override val supportsPrivacyOptions = false
    override fun showSummaryInterstitial(state: AppState, onShown: () -> Unit, onFinished: () -> Unit) = onFinished()
    override fun showBonusRewarded(onRewarded: () -> Unit, onFinished: () -> Unit) = onFinished()
    override fun showPrivacyOptions(onFinished: () -> Unit) = onFinished()
}

fun interstitialEligible(state: AppState, nowEpochMs: Long = Clock.System.now().toEpochMilliseconds()): Boolean {
    val last = state.lastInterstitialShownEpochMs ?: return state.completedWorkoutIds.size > 1
    if (state.completedWorkoutIds.size <= 1) return false
    return nowEpochMs >= last && nowEpochMs - last >= 24 * 60 * 60 * 1000L
}
