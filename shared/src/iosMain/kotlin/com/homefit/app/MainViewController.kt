package com.homefit.app

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.remember
import com.homefit.feature.app.HomeFitViewModel

fun MainViewController() = ComposeUIViewController {
    val viewModel = remember { HomeFitViewModel() }
    HomeFitApp(viewModel)
}
