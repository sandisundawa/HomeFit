package com.homefit

import androidx.lifecycle.viewModelScope
import com.homefit.core.model.*
import com.homefit.feature.app.*
import java.awt.EventQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PlayerTimerTest {
    private val workout = Workout(
        id = "timer-test", name = "Timer test", category = ExerciseCategory.WARM_UP,
        week = 1, slot = 1, durationMinutes = 1,
        exercises = listOf(WorkoutExercise(
            exercise = Exercise("timed", "Timed movement", ExerciseCategory.WARM_UP,
                type = ExerciseType.DURATION, defaultDurationSeconds = 60),
            section = WorkoutSection.WARM_UP, restAfterSeconds = 20,
        )),
    )

    @Test fun `Back resumes paused exercise timer`() = assertResume(AppIntent.Back, rest = false)
    @Test fun `Back resumes paused rest timer`() = assertResume(AppIntent.Back, rest = true)
    @Test fun `Resume resumes paused exercise timer`() = assertResume(AppIntent.ResumePlayer, rest = false)
    @Test fun `Resume resumes paused rest timer`() = assertResume(AppIntent.ResumePlayer, rest = true)

    private fun assertResume(intent: AppIntent, rest: Boolean) = EventQueue.invokeAndWait {
        val model = HomeFitViewModel()
        try {
            model.dispatch(AppIntent.OpenWorkout(workout))
            model.beginSelectedWorkout()
            model.dispatch(AppIntent.StartPlayer)
            if (rest) model.dispatch(AppIntent.CompleteStep)
            val beforePause = model.state.value
            repeat(3) {
                val timer = model.activeJobs().single()
                model.dispatch(AppIntent.PausePlayer)
                assertEquals(AppRoute.PAUSED, model.state.value.route)
                assertFalse(timer.isActive, "Pause must cancel the running timer")
                assertEquals(0, model.activeJobs().size)

                model.dispatch(intent)
                assertEquals(beforePause, model.state.value, "Resume must preserve player and navigation state")
                assertEquals(1, model.activeJobs().size, "Returning to PLAYER must restart exactly one timer")
            }
            model.dispatch(AppIntent.ExitPlayer)
            assertEquals(0, model.activeJobs().size, "Exit must cancel the resumed timer")
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `Back from paused get ready does not start timer`() = EventQueue.invokeAndWait {
        val model = HomeFitViewModel()
        try {
            model.dispatch(AppIntent.OpenWorkout(workout))
            model.beginSelectedWorkout()
            val beforePause = model.state.value
            model.dispatch(AppIntent.PausePlayer)
            model.dispatch(AppIntent.Back)
            assertEquals(beforePause, model.state.value)
            assertEquals(0, model.activeJobs().size)
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `ordinary Back does not start timer`() = EventQueue.invokeAndWait {
        val model = HomeFitViewModel()
        try {
            model.dispatch(AppIntent.SelectTab(AppRoute.HOME))
            model.dispatch(AppIntent.OpenWorkout(workout))
            model.dispatch(AppIntent.Back)
            assertEquals(AppRoute.HOME, model.state.value.route)
            assertEquals(0, model.activeJobs().size)
        } finally {
            model.viewModelScope.cancel()
        }
    }

    @Test fun `countdown ticks after paused Back`() = runBlocking {
        withContext(Dispatchers.Main) {
            val model = HomeFitViewModel()
            try {
                model.dispatch(AppIntent.OpenWorkout(workout))
                model.beginSelectedWorkout()
                model.dispatch(AppIntent.StartPlayer)
                model.dispatch(AppIntent.PausePlayer)
                model.dispatch(AppIntent.Back)
                // Observe an actual tick; the timeout only bounds a stalled timer.
                val tick = withTimeout(10_000) { model.state.first { it.player?.remainingSeconds == 59 } }
                assertEquals(AppRoute.PLAYER, tick.route)
                assertEquals(PlayerPhase.EXERCISE, tick.player?.phase)
            } finally {
                model.viewModelScope.cancel()
            }
        }
    }

    // With no repository, the timer is the only child job of this ViewModel.
    private fun HomeFitViewModel.activeJobs() =
        viewModelScope.coroutineContext[Job]!!.children.filter { it.isActive }.toList()
}
