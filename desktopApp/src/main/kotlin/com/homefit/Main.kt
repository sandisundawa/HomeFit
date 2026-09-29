package com.homefit

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.homefit.app.HomeFitApp
import com.homefit.feature.app.HomeFitViewModel

fun main() = application {
    val viewModel = remember { HomeFitViewModel() }
    Window(
        onCloseRequest = ::exitApplication,
        title = "HomeFit",
        state = WindowState(width = 420.dp, height = 860.dp),
    ) { HomeFitApp(viewModel) }
}
