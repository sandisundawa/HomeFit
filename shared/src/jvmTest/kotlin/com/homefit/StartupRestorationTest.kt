package com.homefit

import androidx.lifecycle.viewModelScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import com.homefit.app.HomeFitApp
import com.homefit.core.data.SeedCatalog
import com.homefit.core.model.FitnessGoal
import com.homefit.feature.app.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalComposeUiApi::class)
class StartupRestorationTest {
    @Test fun `onboarding cannot change state before delayed snapshot loads`() = onMain {
        val repository = DelayedRepository()
        val model = HomeFitViewModel(repository)
        try {
            repository.loadStarted.await()
            val initial = model.state.value
            model.dispatch(AppIntent.FinishSplash)
            model.dispatch(AppIntent.BeginOnboarding)
            model.dispatch(AppIntent.ChooseGoal(FitnessGoal.BUILD_STRENGTH))
            val duringLoad = model.state.value
            assertTrue(repository.savedStates.isEmpty(), "Never save startup actions before loading")

            val saved = AppState(route = AppRoute.HOME, waterDone = true)
            repository.result.complete(saved)
            assertEquals(saved, model.state.first { it == saved })
            assertEquals(initial, duringLoad, "Startup must not accept changes that the snapshot then overwrites")
            assertTrue(repository.savedStates.all { it == saved })
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `workout cannot start before delayed snapshot loads`() = onMain {
        val repository = DelayedRepository()
        val model = HomeFitViewModel(repository)
        try {
            repository.loadStarted.await()
            val initial = model.state.value
            model.dispatch(AppIntent.OpenWorkout(SeedCatalog.workouts.first()))
            model.beginSelectedWorkout()
            model.dispatch(AppIntent.StartPlayer)
            val duringLoad = model.state.value

            val saved = AppState(route = AppRoute.HOME, walkDone = true)
            repository.result.complete(saved)
            assertEquals(saved, model.state.first { it == saved })
            assertEquals(initial, duringLoad, "A workout must not start and then disappear when loading finishes")
            assertTrue(repository.savedStates.all { it == saved })
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `missing snapshot enables onboarding and saves its first change`() = onMain {
        val repository = DelayedRepository()
        val model = HomeFitViewModel(repository)
        try {
            repository.loadStarted.await()
            model.dispatch(AppIntent.FinishSplash)
            assertEquals(AppState(), model.state.value)
            repository.result.complete(null)
            model.restorationStatus.first { it == RestorationStatus.READY }
            assertEquals(AppState(), model.state.value)
            assertTrue(repository.savedStates.isEmpty(), "Do not persist the default startup state")

            model.dispatch(AppIntent.FinishSplash)
            val welcome = AppState(route = AppRoute.WELCOME)
            assertEquals(welcome, model.state.value)
            assertEquals(welcome, repository.saves.first { it.isNotEmpty() }.last())
            model.dispatch(AppIntent.BeginOnboarding)
            assertEquals(AppRoute.GOAL, model.state.value.route)
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `restored paused exercise stays paused and Back preserves player`() =
        assertPausedRestoration(PlayerPhase.EXERCISE)

    @Test fun `restored paused rest stays paused and Back preserves player`() =
        assertPausedRestoration(PlayerPhase.REST)

    private fun assertPausedRestoration(phase: PlayerPhase) = onMain {
        val repository = DelayedRepository()
        val workout = SeedCatalog.workouts.first()
        val saved = AppState(
            route = AppRoute.PAUSED, selectedWorkout = workout,
            player = PlayerState(workout, phase = phase, remainingSeconds = 17),
            navigationStack = listOf(AppRoute.HOME, AppRoute.WORKOUT_DETAIL, AppRoute.PLAYER),
            completedWorkoutIds = setOf("previous-workout"),
        )
        val model = HomeFitViewModel(repository)
        try {
            repository.loadStarted.await()
            model.dispatch(AppIntent.Back)
            repository.result.complete(saved)
            model.restorationStatus.first { it == RestorationStatus.READY }
            assertEquals(saved, model.state.value)
            assertTrue(repository.savedStates.isEmpty(), "Restoration itself must not rewrite the snapshot")

            model.dispatch(AppIntent.Back)
            val resumed = saved.copy(route = AppRoute.PLAYER, navigationStack = saved.navigationStack.dropLast(1))
            assertEquals(resumed, model.state.value)
            assertEquals(resumed, repository.saves.first { it.isNotEmpty() }.last())
            model.dispatch(AppIntent.ExitPlayer)
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `failed load never enables actions or saves default state`() = onMain {
        val repository = DelayedRepository()
        val model = HomeFitViewModel(repository)
        try {
            repository.loadStarted.await()
            repository.result.completeExceptionally(IllegalStateException("Synthetic load failure"))
            model.restorationStatus.first { it == RestorationStatus.FAILED }
            model.dispatch(AppIntent.FinishSplash)
            model.dispatch(AppIntent.OpenWorkout(SeedCatalog.workouts.first()))
            model.beginSelectedWorkout()
            assertEquals(AppState(), model.state.value)
            assertTrue(repository.savedStates.isEmpty())
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `shared UI exposes no actions until restored content is ready`() = assertStartupUi(fail = false)
    @Test fun `shared UI exposes a safe error instead of onboarding after load failure`() = assertStartupUi(fail = true)

    private fun assertStartupUi(fail: Boolean) = onMain {
        val repository = DelayedRepository()
        val model = HomeFitViewModel(repository)
        val scene = ImageComposeScene(width = 390, height = 844) { HomeFitApp(model) }
        try {
            repository.loadStarted.await()
            repeat(3) { scene.render(it * 16_666_667L).close() }
            fun nodes() = scene.semanticsOwners.flatMap { descendants(it.rootSemanticsNode) }
            fun texts() = nodes().flatMap { it.config.getOrNull(SemanticsProperties.Text).orEmpty() }.map { it.text }
            assertTrue("Restoring your workout…" in texts())
            assertTrue(nodes().none { it.config.getOrNull(SemanticsActions.OnClick) != null })

            if (fail) repository.result.completeExceptionally(IllegalStateException("Synthetic load failure"))
            else repository.result.complete(AppState(route = AppRoute.WELCOME))
            model.restorationStatus.first { it != RestorationStatus.LOADING }
            repeat(3) { scene.render(100_000_000L + it * 16_666_667L).close() }
            if (fail) {
                assertTrue(texts().any { it.startsWith("Unable to restore your workout.") })
                assertTrue(nodes().none { it.config.getOrNull(SemanticsActions.OnClick) != null })
            } else {
                val start = nodes().single { node ->
                    node.config.getOrNull(SemanticsActions.OnClick) != null &&
                        node.config.getOrNull(SemanticsProperties.Text)?.any { it.text == "Build my plan" } == true
                }
                start.config[SemanticsActions.OnClick].action!!.invoke()
                assertEquals(AppRoute.GOAL, model.state.value.route)
            }
        } finally {
            scene.close()
            model.viewModelScope.cancel()
        }
    }

    private fun descendants(node: SemanticsNode): List<SemanticsNode> = listOf(node) + node.children.flatMap(::descendants)

    private class DelayedRepository : AppStateRepository {
        val loadStarted = CompletableDeferred<Unit>()
        val result = CompletableDeferred<AppState?>()
        val saves = MutableStateFlow<List<AppState>>(emptyList())
        val savedStates: List<AppState> get() = saves.value

        override suspend fun load(): AppState? {
            loadStarted.complete(Unit)
            return result.await()
        }

        override suspend fun save(state: AppState) { saves.value = saves.value + state }
    }

    private fun onMain(block: suspend () -> Unit) = runBlocking {
        // The timeout only bounds broken awaits; repository completion controls ordering.
        withTimeout(10_000) { withContext(Dispatchers.Main) { block() } }
    }
}
