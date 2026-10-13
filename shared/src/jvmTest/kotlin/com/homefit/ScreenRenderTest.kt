package com.homefit

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import com.homefit.app.HomeFitContent
import com.homefit.core.data.SeedCatalog
import com.homefit.feature.app.*
import java.awt.EventQueue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/** Renders every route with real catalog data, including compact screens at a larger font size. */
@OptIn(ExperimentalComposeUiApi::class)
class ScreenRenderTest {
    @Test fun `all screens render at phone and compact accessible sizes`() {
        EventQueue.invokeAndWait {
            val workout = SeedCatalog.workouts.first()
            val base = AppState(
                program = SeedCatalog.createProgram(setOf(1, 3, 5, 7)),
                selectedWorkout = workout,
                selectedExercise = workout.exercises.first().exercise,
                player = PlayerState(workout),
            )
            val states = AppRoute.entries.map { route ->
                route.name.lowercase() to base.copy(route = route,
                    selectedTab = if (route in listOf(AppRoute.HOME, AppRoute.LIBRARY, AppRoute.PROGRESS, AppRoute.PROFILE)) route else AppRoute.HOME)
            } + listOf(
                "player_rest" to base.copy(route = AppRoute.PLAYER, player = PlayerState(workout, phase = PlayerPhase.REST, remainingSeconds = 20)),
                "history_completed" to base.copy(route = AppRoute.HISTORY, completedWorkoutIds = setOf(workout.id)),
            )
            for ((width, height, fontScale) in listOf(Triple(390, 844, 1f), Triple(320, 700, 1.3f))) {
                for ((name, state) in states) {
                    val scene = ImageComposeScene(width = width, height = height, density = Density(1f, fontScale)) {
                        HomeFitContent(state, dispatch = {}, onStartWorkout = {})
                    }
                    try {
                        repeat(3) { scene.render(it * 16_666_667L).close() }
                        scene.render(50_000_000L).use { image ->
                            image.encodeToData()!!.use { png ->
                                assertTrue(png.size > 1_000, "$name must render visible content")
                                val directory = File("build/reports/screens/$width").apply { mkdirs() }
                                File(directory, "$name.png").writeBytes(png.bytes)
                            }
                        }
                    } finally {
                        scene.close()
                    }
                }
            }
        }
    }
}
