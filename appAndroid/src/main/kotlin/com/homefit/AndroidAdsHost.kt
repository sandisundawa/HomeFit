package com.homefit

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.UserMessagingPlatform
import com.homefit.feature.app.AdsHost
import com.homefit.feature.app.AppState
import com.homefit.feature.app.interstitialEligible

/** Activity-scoped bridge; common code never sees an Android Activity or SDK type. */
class AndroidAdsHost(private val activity: Activity) : AdsHost {
    override val supportsRewarded = true
    override val supportsPrivacyOptions = true
    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null

    fun preload() {
        InterstitialAd.load(activity, BuildConfig.HOMEFIT_INTERSTITIAL_AD_UNIT_ID, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { interstitial = null }
        })
        RewardedAd.load(activity, BuildConfig.HOMEFIT_REWARDED_AD_UNIT_ID, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { rewarded = null }
        })
    }

    override fun showSummaryInterstitial(state: AppState, onShown: () -> Unit, onFinished: () -> Unit) {
        val ad = interstitial ?: run { onFinished(); preload(); return }
        interstitial = null
        var finished = false
        fun finish() { if (!finished) { finished = true; onFinished(); preload() } }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() = onShown()
            override fun onAdDismissedFullScreenContent() = finish()
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) = finish()
        }
        if (interstitialEligible(state)) ad.show(activity) else finish()
    }

    override fun showBonusRewarded(onRewarded: () -> Unit, onFinished: () -> Unit) {
        val ad = rewarded ?: run { onFinished(); preload(); return }
        rewarded = null
        var rewardedOnce = false
        var finished = false
        fun finish() { if (!finished) { finished = true; onFinished(); preload() } }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() = finish()
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) = finish()
        }
        ad.show(activity) {
            if (!rewardedOnce) { rewardedOnce = true; onRewarded() }
        }
    }

    override fun showPrivacyOptions(onFinished: () -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { onFinished() }
    }
}
