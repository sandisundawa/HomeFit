package com.homefit.app

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AndroidAdsRuntime {
    var allowed by mutableStateOf(false)
    var bannerAdUnitId: String = ""
}

@Composable
internal actual fun QuietBannerAd() {
    if (!AndroidAdsRuntime.allowed || AndroidAdsRuntime.bannerAdUnitId.isBlank()) return
    AndroidView(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        factory = { context ->
            AdView(context).apply {
                adUnitId = AndroidAdsRuntime.bannerAdUnitId
                val widthDp = (context.resources.displayMetrics.widthPixels /
                    context.resources.displayMetrics.density).toInt()
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
