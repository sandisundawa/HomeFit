package com.homefit.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.homefit.feature.app.AppRoute

/** Shared, resolution-independent navigation artwork for every platform. */
internal object NavigationIcons {
    val Today = navigationIcon("Today") {
        moveTo(3f, 10f); lineTo(12f, 3f); lineTo(21f, 10f)
        moveTo(5f, 9f); lineTo(5f, 21f); lineTo(10f, 21f)
        lineTo(10f, 14f); lineTo(14f, 14f); lineTo(14f, 21f)
        lineTo(19f, 21f); lineTo(19f, 9f)
    }

    val Workouts = navigationIcon("Workouts") {
        moveTo(8f, 12f); lineTo(16f, 12f)
        moveTo(4f, 7f); lineTo(8f, 7f); lineTo(8f, 17f)
        lineTo(4f, 17f); close()
        moveTo(16f, 7f); lineTo(20f, 7f); lineTo(20f, 17f)
        lineTo(16f, 17f); close()
        moveTo(2f, 10f); lineTo(2f, 14f)
        moveTo(22f, 10f); lineTo(22f, 14f)
    }

    val Progress = navigationIcon("Progress") {
        moveTo(4f, 4f); lineTo(4f, 20f); lineTo(21f, 20f)
        moveTo(8f, 15f); lineTo(8f, 17f)
        moveTo(13f, 12f); lineTo(13f, 17f)
        moveTo(18f, 9f); lineTo(18f, 17f)
        moveTo(7f, 10f); lineTo(12f, 6f); lineTo(15f, 7f); lineTo(20f, 3f)
    }

    val Profile = navigationIcon("Profile") {
        moveTo(16f, 7f)
        arcTo(4f, 4f, 0f, true, true, 8f, 7f)
        arcTo(4f, 4f, 0f, true, true, 16f, 7f)
        close()
        moveTo(4f, 21f); lineTo(4f, 19f)
        curveTo(4f, 15.7f, 7.6f, 14f, 12f, 14f)
        curveTo(16.4f, 14f, 20f, 15.7f, 20f, 19f)
        lineTo(20f, 21f); close()
    }

    fun forRoute(route: AppRoute): ImageVector = when (route) {
        AppRoute.HOME -> Today
        AppRoute.LIBRARY -> Workouts
        AppRoute.PROGRESS -> Progress
        AppRoute.PROFILE -> Profile
        else -> error("Not a bottom-navigation route: $route")
    }
}

private fun navigationIcon(name: String, draw: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = draw,
        )
    }.build()
