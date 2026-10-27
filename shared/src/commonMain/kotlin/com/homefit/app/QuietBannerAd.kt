package com.homefit.app

import androidx.compose.runtime.Composable

/** A deliberately quiet browsing-only ad surface; platform implementations may be empty. */
@Composable
internal expect fun QuietBannerAd()
