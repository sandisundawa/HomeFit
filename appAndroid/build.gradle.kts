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
}
