package com.homefit

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.unit.Density
import com.homefit.app.HomeFitContent
import com.homefit.core.data.SeedCatalog
import com.homefit.feature.app.*
import java.awt.EventQueue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

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
                    val actions = mutableListOf<AppIntent>()
                    var started = false
                    val scene = ImageComposeScene(width = width, height = height, density = Density(1f, fontScale)) {
                        HomeFitContent(state, dispatch = { actions += it }, onStartWorkout = { started = true })
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
                        val nodes = scene.semanticsOwners.flatMap { descendants(it.rootSemanticsNode) }
                        val texts = nodes.flatMap { it.config.getOrNull(SemanticsProperties.Text).orEmpty() }.map { it.text }
                        if (state.route == AppRoute.LIBRARY) {
                            for (week in 1..4) assertTrue("Week $week" in texts, "Library must include week $week")
                            val workoutButtons = nodes.filter { node ->
                                node.config.getOrNull(SemanticsActions.OnClick) != null &&
                                    node.config.getOrNull(SemanticsProperties.Text)?.any { it.text.startsWith("View workout · Week") } == true
                            }
                            assertEquals(SeedCatalog.workouts.size, workoutButtons.size, "Every catalog workout must be reachable")
                            workoutButtons.zip(SeedCatalog.workouts).forEach { (button, expectedWorkout) ->
                                button.config[SemanticsActions.OnClick].action!!.invoke()
                                assertEquals(AppIntent.OpenWorkout(expectedWorkout), actions.last())
                            }
                        }
                        if (state.route == AppRoute.WEEKLY_SCHEDULE) {
                            for (day in 1..28) assertTrue(day.toString() in texts, "Schedule must include day $day")
                            assertEquals(state.program.count { it.workout == null }, texts.count { it == "Recovery day" })
                        }
                        if (state.route == AppRoute.HOME || state.route == AppRoute.WORKOUT_DETAIL) {
                            val start = nodes.single { node ->
                                node.config.getOrNull(SemanticsActions.OnClick) != null &&
                                    node.config.getOrNull(SemanticsProperties.Text)?.any { it.text.startsWith("Start workout") } == true
                            }
                            val bounds = start.boundsInRoot
                            assertTrue(bounds.top >= 0 && bounds.bottom <= height && bounds.height >= 56, "$name start button must be fully visible")
                            start.config[SemanticsActions.OnClick].action!!.invoke()
                            if (state.route == AppRoute.HOME) assertEquals(AppIntent.OpenWorkout(workout), actions.last())
                            else assertTrue(started, "Pinned button must start the selected workout")
                        }
                        if (state.route == AppRoute.HOME) {
                            val today = nodes.single { it.config.getOrNull(SemanticsProperties.ContentDescription)?.any { label -> ", today" in label } == true }
                            val dayName = java.time.LocalDate.now().dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
                            assertTrue(today.config[SemanticsProperties.ContentDescription].single().startsWith(dayName))
                            assertEquals(true, today.config[SemanticsProperties.Selected])
                        }
                    } finally {
                        scene.close()
                    }
                }
            }
        }
    }

    private fun descendants(node: SemanticsNode): List<SemanticsNode> = listOf(node) + node.children.flatMap(::descendants)
}
