package com.homefit

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.homefit.core.model.*
import com.homefit.feature.app.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

class BackgroundPauseTest {
    @Test fun `background does not discard delayed onboarding effects`() = onMain {
        val model = HomeFitViewModel()
        try {
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.FinishSplash)
            assertEquals(AppRoute.WELCOME, model.state.value.route)
            model.dispatch(AppIntent.ConfirmReminder)
            model.dispatch(AppIntent.CreatePlan)
            assertEquals(AppRoute.PLAN_READY, model.state.value.route)
            assertTrue(model.state.value.program.isNotEmpty())
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(AppRoute.PLAN_READY, model.state.value.route)
        } finally { model.viewModelScope.cancel() }
    }

    private val workout = Workout(
        "background-test", "Background test", ExerciseCategory.WARM_UP, 1, 1, 1,
        listOf(WorkoutExercise(
            Exercise("timed", "Timed movement", ExerciseCategory.WARM_UP,
                type = ExerciseType.DURATION, defaultDurationSeconds = 60),
            WorkoutSection.WARM_UP, restAfterSeconds = 20,
        )),
    )

    @Test fun `running exercise backgrounds without losing progress`() = assertBackground(rest = false)
    @Test fun `running rest backgrounds without losing progress`() = assertBackground(rest = true)

    private fun assertBackground(rest: Boolean) = onMain {
        val model = runningModel(rest)
        try {
            val before = model.state.value
            val timer = model.activeJobs().single()
            model.dispatch(AppIntent.AppBackgrounded)
            val paused = before.copy(route = AppRoute.PAUSED,
                player = before.player!!.copy(pausedByBackground = true),
                navigationStack = before.navigationStack + AppRoute.PLAYER)
            assertEquals(paused, model.state.value)
            assertFalse(timer.isActive)
            assertTrue(model.activeJobs().isEmpty())
            repeat(5) {
                model.dispatch(AppIntent.AppBackgrounded)
                model.dispatch(AppIntent.AppForegrounded)
                assertEquals(paused, model.state.value)
                assertTrue(model.activeJobs().isEmpty())
            }
            model.dispatch(AppIntent.Back)
            assertEquals(paused, model.state.value, "Background pause requires the Resume button, not Back")
            model.dispatch(AppIntent.ResumePlayer)
            assertEquals(before, model.state.value)
            val resumedTimer = model.activeJobs().single()
            model.dispatch(AppIntent.AppForegrounded)
            assertSame(resumedTimer, model.activeJobs().single(), "Foreground must not restart an existing job")
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `manually paused session stays unchanged on background`() = onMain {
        val model = runningModel()
        try {
            model.dispatch(AppIntent.PausePlayer)
            val paused = model.state.value
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(paused, model.state.value)
            assertTrue(model.activeJobs().isEmpty())
            model.dispatch(AppIntent.Back)
            assertEquals(AppRoute.PLAYER, model.state.value.route)
            assertEquals(1, model.activeJobs().size)
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `get ready and reps only sessions do not become background pauses`() = onMain {
        val model = HomeFitViewModel()
        try {
            val repsWorkout = workout.copy(exercises = listOf(workout.exercises.first().copy(durationSeconds = null, reps = 8)))
            model.dispatch(AppIntent.OpenWorkout(repsWorkout))
            model.beginSelectedWorkout()
            val ready = model.state.value
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(ready, model.state.value)
            model.dispatch(AppIntent.StartPlayer)
            val reps = model.state.value
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(reps, model.state.value)
            assertTrue(model.activeJobs().isEmpty())
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `finished session never restarts on visibility events`() = onMain {
        val model = runningModel()
        try {
            model.dispatch(AppIntent.CompleteStep)
            model.dispatch(AppIntent.SkipRest)
            val finished = model.state.value
            assertEquals(PlayerPhase.FINISHED, finished.player?.phase)
            repeat(3) {
                model.dispatch(AppIntent.AppBackgrounded)
                model.dispatch(AppIntent.AppForegrounded)
            }
            assertEquals(finished, model.state.value)
            assertTrue(model.activeJobs().isEmpty())
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `explicit Resume continues the countdown after foreground`() = onMain {
        val model = runningModel()
        try {
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(AppRoute.PAUSED, model.state.value.route)
            model.dispatch(AppIntent.ResumePlayer)
            val tick = model.state.first { it.player?.remainingSeconds == 59 }
            assertEquals(AppRoute.PLAYER, tick.route)
            assertFalse(tick.player!!.pausedByBackground)
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `visibility events during delayed restoration cannot start a hidden timer`() = onMain {
        val repository = MemoryRepository()
        val model = HomeFitViewModel(repository)
        try {
            repository.started.await()
            model.dispatch(AppIntent.AppBackgrounded)
            model.dispatch(AppIntent.AppForegrounded)
            model.dispatch(AppIntent.AppBackgrounded)
            val saved = activeSnapshot()
            repository.result.complete(saved)
            model.restorationStatus.first { it == RestorationStatus.READY }
            assertEquals(AppRoute.PAUSED, model.state.value.route)
            assertEquals(saved.player!!.remainingSeconds, model.state.value.player!!.remainingSeconds)
            assertTrue(model.state.value.player!!.pausedByBackground)
            val paused = model.state.value
            assertEquals(paused, repository.saves.first { it.isNotEmpty() }.last())
            model.dispatch(AppIntent.ResumePlayer)
            assertEquals(paused, model.state.value, "Hidden app must not resume")
            model.dispatch(AppIntent.AppForegrounded)
            assertEquals(paused, model.state.value)
        } finally { model.viewModelScope.cancel() }
    }

    @Test fun `background flag and remaining time survive persistence and recreation`() = onMain {
        val repository = MemoryRepository()
        val model = HomeFitViewModel(repository)
        var recreated: HomeFitViewModel? = null
        try {
            repository.result.complete(activeSnapshot().copy(route = AppRoute.PAUSED,
                navigationStack = listOf(AppRoute.HOME, AppRoute.PLAYER)))
            model.restorationStatus.first { it == RestorationStatus.READY }
            model.dispatch(AppIntent.ResumePlayer)
            model.dispatch(AppIntent.AppBackgrounded)
            val paused = model.state.value
            assertTrue(paused.player!!.pausedByBackground)
            repository.saves.first { it.lastOrNull() == paused }
            model.viewModelScope.cancel()
            val decoded = Json.decodeFromString<AppState>(Json.encodeToString(paused))
            val nextRepository = MemoryRepository().apply { result.complete(decoded) }
            val next = HomeFitViewModel(nextRepository)
            recreated = next
            next.restorationStatus.first { it == RestorationStatus.READY }
            next.dispatch(AppIntent.AppForegrounded)
            next.dispatch(AppIntent.Back)
            assertEquals(paused, next.state.value)
            assertTrue(nextRepository.saves.value.isEmpty())
            next.dispatch(AppIntent.ResumePlayer)
            assertEquals(17, next.state.value.player?.remainingSeconds)
            assertEquals(AppRoute.PLAYER, next.state.value.route)
        } finally {
            model.viewModelScope.cancel()
            recreated?.viewModelScope?.cancel()
        }
    }

    @Test fun `retained ViewModel does not reset timer when host reattaches`() = onMain {
        val store = ViewModelStore()
        val model = runningModel()
        store.put("workout", model)
        try {
            val before = model.state.value
            val timer = model.activeJobs().single()
            // Android skips background dispatch for configuration changes and retains this store.
            val reattached = store["workout"] as HomeFitViewModel
            reattached.dispatch(AppIntent.AppForegrounded)
            assertSame(model, reattached)
            assertEquals(before, reattached.state.value)
            assertSame(timer, reattached.activeJobs().single())
        } finally { store.clear() }
        assertTrue(model.activeJobs().isEmpty())
    }

    @Test fun `old player JSON defaults to manual pause behavior`() {
        val oldJson = Json.encodeToString(activeSnapshot().copy(route = AppRoute.PAUSED))
        assertFalse(oldJson.contains("pausedByBackground"))
        assertFalse(Json.decodeFromString<AppState>(oldJson).player!!.pausedByBackground)
    }

    private fun activeSnapshot() = AppState(route = AppRoute.PLAYER, selectedWorkout = workout,
        player = PlayerState(workout, phase = PlayerPhase.EXERCISE, remainingSeconds = 17),
        navigationStack = listOf(AppRoute.HOME))

    private fun runningModel(rest: Boolean = false) = HomeFitViewModel().apply {
        dispatch(AppIntent.OpenWorkout(workout))
        beginSelectedWorkout()
        dispatch(AppIntent.StartPlayer)
        if (rest) dispatch(AppIntent.CompleteStep)
    }

    private class MemoryRepository : AppStateRepository {
        val started = CompletableDeferred<Unit>()
        val result = CompletableDeferred<AppState?>()
        val saves = MutableStateFlow<List<AppState>>(emptyList())
        override suspend fun load(): AppState? { started.complete(Unit); return result.await() }
        override suspend fun save(state: AppState) { saves.value += state }
    }

    private fun HomeFitViewModel.activeJobs() = viewModelScope.coroutineContext[Job]!!.children.filter { it.isActive }.toList()
    private fun onMain(block: suspend () -> Unit) = runBlocking {
        withTimeout(10_000) { withContext(Dispatchers.Main) { block() } }
    }
}
