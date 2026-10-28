package com.homefit.app

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewModelScope
import com.homefit.feature.app.AppIntent
import com.homefit.feature.app.HomeFitViewModel
import kotlinx.coroutines.cancel
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification

fun MainViewController() = ComposeUIViewController {
    val viewModel = remember { HomeFitViewModel() }
    DisposableEffect(viewModel) {
        val center = NSNotificationCenter.defaultCenter
        val background = center.addObserverForName(
            UIApplicationDidEnterBackgroundNotification, `object` = null, queue = NSOperationQueue.mainQueue,
        ) { viewModel.dispatch(AppIntent.AppBackgrounded) }
        val foreground = center.addObserverForName(
            UIApplicationWillEnterForegroundNotification, `object` = null, queue = NSOperationQueue.mainQueue,
        ) { viewModel.dispatch(AppIntent.AppForegrounded) }
        viewModel.dispatch(if (UIApplication.sharedApplication.applicationState == UIApplicationState.UIApplicationStateBackground)
            AppIntent.AppBackgrounded else AppIntent.AppForegrounded)
        onDispose {
            center.removeObserver(background)
            center.removeObserver(foreground)
            viewModel.viewModelScope.cancel()
        }
    }
    HomeFitApp(viewModel)
}
