package com.homefit.feature.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homefit.core.data.SeedCatalog
import com.homefit.core.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
enum class AppRoute {
    SPLASH, WELCOME, GOAL, FITNESS_LEVEL, FREQUENCY, DAYS, PREFERENCES, RESTRICTIONS,
    REMINDER_SETUP, CREATING_PLAN, PLAN_READY, HOME, WEEKLY_SCHEDULE, WORKOUT_DETAIL,
    EXERCISE_DETAIL, LIBRARY, WORKOUT_CATEGORY, PROGRESS, HISTORY, HABITS_DETAIL, PROFILE,
    EDIT_GOAL, SCHEDULE_SETTINGS, REMINDER_SETTINGS, SETTINGS, PLAYER, PAUSED, COMPLETE,
    WORKOUT_RESULT,
}

@Serializable enum class PlayerPhase { GET_READY, EXERCISE, REST, FINISHED }

@Serializable
data class PlayerState(
    val workout: Workout,
    val exerciseIndex: Int = 0,
    val phase: PlayerPhase = PlayerPhase.GET_READY,
    val remainingSeconds: Int = 5,
    val completedExerciseIds: Set<String> = emptySet(),
) { val current: WorkoutExercise get() = workout.exercises[exerciseIndex] }

@Serializable
data class AppState(
    val route: AppRoute = AppRoute.SPLASH,
    val profile: UserProfile = UserProfile(),
    val program: List<ProgramDay> = emptyList(),
    val selectedTab: AppRoute = AppRoute.HOME,
    val player: PlayerState? = null,
    val completedWorkoutIds: Set<String> = emptySet(),
    val waterDone: Boolean = false,
    val walkDone: Boolean = false,
    val selectedWorkout: Workout? = null,
    val selectedExercise: Exercise? = null,
    val navigationStack: List<AppRoute> = emptyList(),
)

sealed interface AppIntent {
    data object BeginOnboarding : AppIntent; data object FinishSplash : AppIntent
    data class ChooseGoal(val goal: FitnessGoal) : AppIntent; data class ChooseLevel(val level: FitnessLevel) : AppIntent
    data class ChooseFrequency(val days: Int) : AppIntent; data class ToggleDay(val day: Int) : AppIntent
    data object ConfirmDays : AppIntent; data object ConfirmPreferences : AppIntent; data object ConfirmRestrictions : AppIntent; data object ConfirmReminder : AppIntent
    data class SetLowImpact(val enabled: Boolean) : AppIntent; data object CreatePlan : AppIntent; data object EnterPlan : AppIntent
    data class SelectTab(val route: AppRoute) : AppIntent; data class OpenWorkout(val workout: Workout) : AppIntent; data class OpenExercise(val exercise: Exercise) : AppIntent
    data class Navigate(val route: AppRoute) : AppIntent; data object Back : AppIntent; data object StartPlayer : AppIntent; data object PausePlayer : AppIntent; data object ResumePlayer : AppIntent
    data object CompleteStep : AppIntent; data object SkipRest : AppIntent; data object FinishWorkout : AppIntent; data object ShowWorkoutResult : AppIntent; data object ExitPlayer : AppIntent
    data object ToggleWater : AppIntent; data object ToggleWalk : AppIntent; data object ResetApp : AppIntent
}

class HomeFitViewModel(private val repository: AppStateRepository? = null) : ViewModel() {
    private val mutableState = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = mutableState.asStateFlow()
    private var timerJob: Job? = null
    private var restored = repository == null

    init {
        if (repository != null) {
            viewModelScope.launch { repository.load()?.let { mutableState.value = it }; restored = true; restartTimerIfNeeded() }
            viewModelScope.launch { state.drop(1).collect { if (restored) repository.save(it) } }
        }
    }

    fun dispatch(intent: AppIntent) {
        mutableState.update { reduce(it, intent) }
        when (intent) {
            AppIntent.StartPlayer, AppIntent.CompleteStep, AppIntent.SkipRest, AppIntent.ResumePlayer -> restartTimerIfNeeded()
            AppIntent.PausePlayer, AppIntent.ExitPlayer, AppIntent.FinishWorkout -> timerJob?.cancel()
            else -> Unit
        }
    }

    private fun reduce(s: AppState, i: AppIntent): AppState = when (i) {
        AppIntent.FinishSplash -> s.copy(route = AppRoute.WELCOME)
        AppIntent.BeginOnboarding -> s.copy(route = AppRoute.GOAL)
        is AppIntent.ChooseGoal -> s.copy(profile = s.profile.copy(goal = i.goal), route = AppRoute.FITNESS_LEVEL)
        is AppIntent.ChooseLevel -> s.copy(profile = s.profile.copy(level = i.level), route = AppRoute.FREQUENCY)
        is AppIntent.ChooseFrequency -> s.copy(profile = s.profile.copy(workoutDays = (1..i.days).toSet()), route = AppRoute.DAYS)
        is AppIntent.ToggleDay -> { val days = s.profile.workoutDays.toMutableSet().apply { if (!add(i.day)) remove(i.day) }; if (days.size in 3..5) s.copy(profile = s.profile.copy(workoutDays = days)) else s }
        AppIntent.ConfirmDays -> s.copy(route = AppRoute.PREFERENCES); AppIntent.ConfirmPreferences -> s.copy(route = AppRoute.RESTRICTIONS)
        AppIntent.ConfirmRestrictions -> s.copy(route = AppRoute.REMINDER_SETUP); AppIntent.ConfirmReminder -> s.copy(route = AppRoute.CREATING_PLAN)
        is AppIntent.SetLowImpact -> s.copy(profile = s.profile.copy(lowImpact = i.enabled))
        AppIntent.CreatePlan -> s.copy(program = SeedCatalog.createProgram(s.profile.workoutDays), route = AppRoute.PLAN_READY)
        AppIntent.EnterPlan -> s.copy(route = AppRoute.HOME, selectedTab = AppRoute.HOME, navigationStack = emptyList())
        is AppIntent.SelectTab -> s.copy(route = i.route, selectedTab = i.route, navigationStack = emptyList())
        is AppIntent.Navigate -> s.push(i.route); AppIntent.Back -> s.pop()
        is AppIntent.OpenWorkout -> s.push(AppRoute.WORKOUT_DETAIL).copy(selectedWorkout = i.workout)
        is AppIntent.OpenExercise -> s.push(AppRoute.EXERCISE_DETAIL).copy(selectedExercise = i.exercise)
        AppIntent.StartPlayer -> s.copy(player = s.player?.copy(phase = PlayerPhase.EXERCISE, remainingSeconds = s.player.current.durationSeconds ?: 0))
        AppIntent.PausePlayer -> s.push(AppRoute.PAUSED)
        AppIntent.ResumePlayer -> s.copy(route = AppRoute.PLAYER, navigationStack = s.navigationStack.dropLast(1))
        AppIntent.CompleteStep, AppIntent.SkipRest -> advancePlayer(s)
        AppIntent.FinishWorkout -> { val id = s.player?.workout?.id ?: return s; s.copy(route = AppRoute.COMPLETE, completedWorkoutIds = s.completedWorkoutIds + id) }
        AppIntent.ShowWorkoutResult -> s.copy(route = AppRoute.WORKOUT_RESULT)
        AppIntent.ExitPlayer -> s.copy(route = AppRoute.HOME, selectedTab = AppRoute.HOME, player = null, navigationStack = emptyList())
        AppIntent.ToggleWater -> s.copy(waterDone = !s.waterDone); AppIntent.ToggleWalk -> s.copy(walkDone = !s.walkDone); AppIntent.ResetApp -> AppState()
    }

    fun beginSelectedWorkout() { val workout = state.value.selectedWorkout ?: return; mutableState.update { it.push(AppRoute.PLAYER).copy(player = PlayerState(workout)) } }

    private fun advancePlayer(s: AppState): AppState {
        val p = s.player ?: return s
        if (p.phase == PlayerPhase.EXERCISE && p.current.restAfterSeconds > 0) return s.copy(player = p.copy(phase = PlayerPhase.REST, remainingSeconds = p.current.restAfterSeconds, completedExerciseIds = p.completedExerciseIds + p.current.exercise.id))
        val next = p.exerciseIndex + 1
        return if (next >= p.workout.exercises.size) s.copy(player = p.copy(phase = PlayerPhase.FINISHED)) else { val e = p.workout.exercises[next]; s.copy(player = p.copy(exerciseIndex = next, phase = PlayerPhase.EXERCISE, remainingSeconds = e.durationSeconds ?: 0)) }
    }

    private fun restartTimerIfNeeded() {
        timerJob?.cancel(); val p = state.value.player ?: return
        if (state.value.route != AppRoute.PLAYER || p.phase !in setOf(PlayerPhase.EXERCISE, PlayerPhase.REST) || p.remainingSeconds <= 0) return
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000); val current = state.value.player ?: return@launch
                if (state.value.route != AppRoute.PLAYER || current.phase !in setOf(PlayerPhase.EXERCISE, PlayerPhase.REST)) return@launch
                if (current.remainingSeconds > 1) mutableState.update { it.copy(player = current.copy(remainingSeconds = current.remainingSeconds - 1)) }
                else { mutableState.update { advancePlayer(it.copy(player = current.copy(remainingSeconds = 0))) }; restartTimerIfNeeded(); return@launch }
            }
        }
    }
}

private fun AppState.push(destination: AppRoute) = copy(route = destination, navigationStack = navigationStack + route)
private fun AppState.pop() = copy(route = navigationStack.lastOrNull() ?: AppRoute.HOME, navigationStack = navigationStack.dropLast(1))
typealias AppStore = HomeFitViewModel
