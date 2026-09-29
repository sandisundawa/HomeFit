plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.desktop)
}

compose.desktop.application {
    mainClass = "com.homefit.MainKt"
}
