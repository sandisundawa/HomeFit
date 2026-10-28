package com.homefit

import android.os.Bundle
import android.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room3.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.homefit.app.AndroidAdsRuntime
import com.homefit.app.HomeFitApp
import com.homefit.core.database.AppDatabase
import com.homefit.core.database.RoomAppStateRepository
import com.homefit.core.database.withHomeFitDefaults
import com.homefit.feature.app.HomeFitViewModel
import com.homefit.feature.app.AppIntent

class MainActivity : ComponentActivity() {
    private lateinit var adsHost: AndroidAdsHost
    private val database by lazy {
        Room.databaseBuilder<AppDatabase>(applicationContext, "homefit.db")
            .withHomeFitDefaults()
            .build()
    }
    private val repository by lazy { RoomAppStateRepository(database) }
    private val appViewModel by lazy {
        val factory = viewModelFactory { initializer { HomeFitViewModel(repository) } }
        ViewModelProvider(this, factory)[HomeFitViewModel::class]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsHost = AndroidAdsHost(this)
        AndroidAdsRuntime.bannerAdUnitId = BuildConfig.HOMEFIT_BANNER_AD_UNIT_ID
        requestConsentAndInitializeAds()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (appViewModel.state.value.route == com.homefit.feature.app.AppRoute.HOME) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("Are you sure want to close the app?")
                        .setPositiveButton("Yes") { _, _ -> finish() }
                        .setNegativeButton("No", null)
                        .show()
                } else {
                    appViewModel.dispatch(com.homefit.feature.app.AppIntent.Back)
                }
            }
        })
        setContent { HomeFitApp(appViewModel, adsHost) }
    }

    private fun requestConsentAndInitializeAds() {
        val information = UserMessagingPlatform.getConsentInformation(this)
        val initializeIfAllowed = {
            if (information.canRequestAds()) {
                AndroidAdsRuntime.allowed = true
                MobileAds.initialize(this) { adsHost.preload() }
            }
        }
        information.requestConsentInfoUpdate(
            this,
            ConsentRequestParameters.Builder().build(),
            { UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { initializeIfAllowed() } },
            { initializeIfAllowed() },
        )
    }

    override fun onStart() {
        super.onStart()
        appViewModel.dispatch(AppIntent.AppForegrounded)
    }

    override fun onStop() {
        // Losing focus can leave the app visible; rotation retains the same ViewModel.
        if (!isChangingConfigurations) appViewModel.dispatch(AppIntent.AppBackgrounded)
        super.onStop()
    }
}
