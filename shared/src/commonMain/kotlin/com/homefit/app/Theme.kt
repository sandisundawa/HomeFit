package com.homefit.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF17221D)
val Moss = Color(0xFF4E6B52)
val Lime = Color(0xFFD9F074)
val Cream = Color(0xFFF6F5F0)
val Clay = Color(0xFFE97855)
val Mist = Color(0xFFE5EAE4)

// Keep the original palette above for the unchanged trainer demonstrations.
val TextPrimary = Color(0xFF191919)
val TextSecondary = Color(0xFF666666)
val Accent = Color(0xFFFF7900)
val AccentText = Color(0xFFAD4800)
val AccentSoft = Color(0xFFFFEBDD)
val PageBackground = Color.White
val SurfaceMuted = Color(0xFFF2F2F2)
val Outline = Color(0xFFE5E5E5)

@Composable
fun HomeFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AccentText,
            onPrimary = Color.White,
            primaryContainer = AccentSoft,
            onPrimaryContainer = AccentText,
            secondary = TextPrimary,
            onSecondary = Color.White,
            secondaryContainer = SurfaceMuted,
            onSecondaryContainer = TextPrimary,
            background = PageBackground,
            onBackground = TextPrimary,
            surface = Color.White,
            onSurface = TextPrimary,
            surfaceVariant = SurfaceMuted,
            onSurfaceVariant = TextSecondary,
            surfaceContainer = SurfaceMuted,
            surfaceContainerHigh = SurfaceMuted,
            outline = TextSecondary,
            outlineVariant = Outline,
            tertiary = AccentText,
        ),
        shapes = Shapes(
            small = RoundedCornerShape(16.dp),
            medium = RoundedCornerShape(24.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(32.dp),
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
            headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-.5).sp),
            titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
            titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
            bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
            labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        ),
        content = content,
    )
}
