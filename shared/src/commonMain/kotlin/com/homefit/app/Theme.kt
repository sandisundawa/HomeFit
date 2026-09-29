package com.homefit.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF17221D)
val Moss = Color(0xFF4E6B52)
val Lime = Color(0xFFD9F074)
val Cream = Color(0xFFF6F5F0)
val Clay = Color(0xFFE97855)
val Mist = Color(0xFFE5EAE4)

@Composable
fun HomeFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Moss,
            onPrimary = Color.White,
            secondary = Lime,
            onSecondary = Ink,
            background = Cream,
            surface = Color.White,
            onSurface = Ink,
            tertiary = Clay,
        ),
        content = content,
    )
}
