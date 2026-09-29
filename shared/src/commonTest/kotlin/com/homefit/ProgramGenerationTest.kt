package com.homefit

import com.homefit.core.data.SeedCatalog
import com.homefit.feature.app.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgramGenerationTest {
    @Test fun `onboarding reducer reaches generated plan`() {
        val viewModel = HomeFitViewModel()
        viewModel.dispatch(AppIntent.FinishSplash)
        viewModel.dispatch(AppIntent.BeginOnboarding)
        viewModel.dispatch(AppIntent.ChooseGoal(com.homefit.core.model.FitnessGoal.WEIGHT_LOSS))
        viewModel.dispatch(AppIntent.ChooseLevel(com.homefit.core.model.FitnessLevel.BEGINNER))
        viewModel.dispatch(AppIntent.ChooseFrequency(4))
        viewModel.dispatch(AppIntent.ConfirmDays)
        viewModel.dispatch(AppIntent.ConfirmPreferences)
        viewModel.dispatch(AppIntent.ConfirmRestrictions)
        viewModel.dispatch(AppIntent.ConfirmReminder)
        viewModel.dispatch(AppIntent.CreatePlan)
        assertEquals(AppRoute.PLAN_READY, viewModel.state.value.route)
        assertEquals(28, viewModel.state.value.program.size)
    }

    @Test fun `catalog contains forty reviewed exercises`() {
        assertEquals(40, SeedCatalog.exercises.size)
        assertEquals(40, SeedCatalog.exercises.map { it.id }.toSet().size)
    }

    @Test fun `four selected weekdays create sixteen workouts`() {
        val program = SeedCatalog.createProgram(setOf(1, 3, 5, 7))
        assertEquals(28, program.size)
        assertEquals(16, program.count { it.workout != null })
        assertEquals(listOf(1, 3, 5, 7), program.take(7).filter { it.workout != null }.map { it.weekday })
    }

    @Test fun `player advances through rest into next movement`() {
        val store = HomeFitViewModel()
        store.dispatch(AppIntent.OpenWorkout(SeedCatalog.workouts.first()))
        store.beginSelectedWorkout()
        store.dispatch(AppIntent.StartPlayer)
        store.dispatch(AppIntent.CompleteStep)
        assertEquals(PlayerPhase.REST, store.state.value.player?.phase)
        store.dispatch(AppIntent.SkipRest)
        assertEquals(1, store.state.value.player?.exerciseIndex)
        assertTrue(store.state.value.player?.completedExerciseIds?.isNotEmpty() == true)
    }

    @Test fun `back navigation unwinds nested workout screens`() {
        val store = HomeFitViewModel()
        val workout = SeedCatalog.workouts.first()
        store.dispatch(AppIntent.SelectTab(AppRoute.HOME))
        store.dispatch(AppIntent.OpenWorkout(workout))
        store.dispatch(AppIntent.OpenExercise(workout.exercises.first().exercise))

        store.dispatch(AppIntent.Back)
        assertEquals(AppRoute.WORKOUT_DETAIL, store.state.value.route)

        store.dispatch(AppIntent.Back)
        assertEquals(AppRoute.HOME, store.state.value.route)
    }
}
