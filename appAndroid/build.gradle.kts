import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.homefit"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.homefit"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
        // Official Google demo IDs are the safe default. Supply -P homefit* values for release.
        manifestPlaceholders["homefitAdmobAppId"] = providers.gradleProperty("homefitAdmobAppId").orElse("ca-app-pub-3940256099942544~3347511713").get()
        buildConfigField("String", "HOMEFIT_BANNER_AD_UNIT_ID", "\"${providers.gradleProperty("homefitBannerAdUnitId").orElse("ca-app-pub-3940256099942544/6300978111").get()}\"")
        buildConfigField("String", "HOMEFIT_INTERSTITIAL_AD_UNIT_ID", "\"${providers.gradleProperty("homefitInterstitialAdUnitId").orElse("ca-app-pub-3940256099942544/1033173712").get()}\"")
        buildConfigField("String", "HOMEFIT_REWARDED_AD_UNIT_ID", "\"${providers.gradleProperty("homefitRewardedAdUnitId").orElse("ca-app-pub-3940256099942544/5224354917").get()}\"")
    }
    buildFeatures { buildConfig = true }
    buildTypes {
        release {
            val configured = listOf("homefitAdmobAppId", "homefitBannerAdUnitId", "homefitInterstitialAdUnitId", "homefitRewardedAdUnitId")
                .map { providers.gradleProperty(it).orNull }
            if (gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) } &&
                configured.any { it.isNullOrBlank() || it?.contains("3940256099942544") == true }) {
                throw GradleException("Release ads require non-demo HomeFit AdMob IDs via -P homefit* properties")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { compilerOptions.jvmTarget = JvmTarget.JVM_17 }

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
}
