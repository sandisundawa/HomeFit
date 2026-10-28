package com.homefit.feature.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homefit.core.data.SeedCatalog
import com.homefit.core.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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
    val completionRecorded: Boolean = false,
    val pausedByBackground: Boolean = false,
    val isBonusWorkout: Boolean = false,
) { val current: WorkoutExercise get() = workout.exercises[exerciseIndex] }

@Serializable
data class AppState(
    val route: AppRoute = AppRoute.SPLASH,
    val profile: UserProfile = UserProfile(),
    val program: List<ProgramDay> = emptyList(),
    val selectedTab: AppRoute = AppRoute.HOME,
    val player: PlayerState? = null,
    val completedWorkoutIds: Set<String> = emptySet(),
    val earlierWorkoutIds: Set<String> = completedWorkoutIds,
    val workoutHistory: List<CompletedWorkout> = emptyList(),
    val historyDate: LocalDate? = null,
    val waterDone: Boolean = false,
    val walkDone: Boolean = false,
    val selectedWorkout: Workout? = null,
    val selectedExercise: Exercise? = null,
    val navigationStack: List<AppRoute> = emptyList(),
    val lastInterstitialShownEpochMs: Long? = null,
    val bonusWorkoutUnlocked: Boolean = false,
)

sealed interface AppIntent {
    data object AppBackgrounded : AppIntent
    data object AppForegrounded : AppIntent
    data class SelectHistoryDate(val date: LocalDate) : AppIntent
    data class ChangeHistoryMonth(val offset: Int) : AppIntent
    data object HistoryToday : AppIntent
    data object BeginOnboarding : AppIntent; data object FinishSplash : AppIntent
    data class ChooseGoal(val goal: FitnessGoal) : AppIntent; data class ChooseLevel(val level: FitnessLevel) : AppIntent
    data class ChooseFrequency(val days: Int) : AppIntent; data class ToggleDay(val day: Int) : AppIntent
    data object ConfirmDays : AppIntent; data object ConfirmPreferences : AppIntent; data object ConfirmRestrictions : AppIntent; data object ConfirmReminder : AppIntent
    data class SetLowImpact(val enabled: Boolean) : AppIntent; data object CreatePlan : AppIntent; data object EnterPlan : AppIntent
    data class SelectTab(val route: AppRoute) : AppIntent; data class OpenWorkout(val workout: Workout) : AppIntent; data class OpenExercise(val exercise: Exercise) : AppIntent
    data class Navigate(val route: AppRoute) : AppIntent; data object Back : AppIntent; data object StartPlayer : AppIntent; data object PausePlayer : AppIntent; data object ResumePlayer : AppIntent
    data object CompleteStep : AppIntent; data object SkipRest : AppIntent; data object FinishWorkout : AppIntent; data object ShowWorkoutResult : AppIntent; data object ExitPlayer : AppIntent
    data class RecordInterstitialShown(val epochMs: Long) : AppIntent
    data object GrantBonusWorkout : AppIntent
    data object OpenBonusWorkout : AppIntent
    data object ToggleWater : AppIntent; data object ToggleWalk : AppIntent; data object ResetApp : AppIntent
}

internal enum class RestorationStatus { LOADING, READY, FAILED }

class HomeFitViewModel(private val repository: AppStateRepository? = null) : ViewModel() {
    private val mutableState = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = mutableState.asStateFlow()
    private var timerJob: Job? = null
    private var isAppVisible = true
    private val timerTick = CountdownTick()
    private val mutableRestorationStatus = MutableStateFlow(if (repository == null) RestorationStatus.READY else RestorationStatus.LOADING)
    internal val restorationStatus: StateFlow<RestorationStatus> = mutableRestorationStatus.asStateFlow()

    init {
        if (repository != null) {
            viewModelScope.launch {
                val snapshot = try {
                    repository.load()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableRestorationStatus.value = RestorationStatus.FAILED
                    return@launch
                }
                snapshot?.let { mutableState.value = it }
                // Subscribe before enabling actions, without writing the initial snapshot.
                launch(start = CoroutineStart.UNDISPATCHED) { state.drop(1).collect { repository.save(it) } }
                // A new process cannot safely infer workout progress while it was absent.
                mutableState.update { it.pauseForBackground() }
                restartTimerIfNeeded()
                mutableRestorationStatus.value = RestorationStatus.READY
            }
        }
    }

    fun dispatch(intent: AppIntent) {
        when (intent) {
            AppIntent.AppBackgrounded -> isAppVisible = false
            AppIntent.AppForegrounded -> { isAppVisible = true; return }
            AppIntent.StartPlayer, AppIntent.ResumePlayer, AppIntent.CompleteStep, AppIntent.SkipRest -> if (!isAppVisible) return
            else -> Unit
        }
        if (restorationStatus.value != RestorationStatus.READY) return
        if (intent == AppIntent.ResumePlayer && (state.value.route != AppRoute.PAUSED || state.value.player?.phase == PlayerPhase.FINISHED)) return
        val previousRoute = mutableState.value.route
        mutableState.update { reduce(it, intent) }
        when (intent) {
            AppIntent.StartPlayer, AppIntent.CompleteStep, AppIntent.SkipRest -> restartTimerIfNeeded()
            AppIntent.ResumePlayer -> restartTimerIfNeeded(resetTick = false)
            AppIntent.PausePlayer, AppIntent.AppBackgrounded -> { timerTick.pause(); timerJob?.cancel() }
            AppIntent.ExitPlayer, AppIntent.FinishWorkout -> { timerJob?.cancel(); timerTick.reset() }
            AppIntent.Back -> if (previousRoute == AppRoute.PAUSED && state.value.route == AppRoute.PLAYER) restartTimerIfNeeded(resetTick = false)
            else -> Unit
        }
    }

    private fun reduce(s: AppState, i: AppIntent): AppState = when (i) {
        AppIntent.AppBackgrounded -> s.pauseForBackground()
        AppIntent.AppForegrounded -> s
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
        is AppIntent.Navigate -> s.push(i.route).let { if (i.route == AppRoute.HISTORY) it.copy(historyDate = today()) else it }
        AppIntent.Back -> if (s.route == AppRoute.PAUSED && s.player?.pausedByBackground == true) s else s.pop()
        is AppIntent.SelectHistoryDate -> s.copy(historyDate = i.date)
        is AppIntent.ChangeHistoryMonth -> s.copy(historyDate = shiftHistoryMonth(s.historyDate ?: today(), i.offset))
        AppIntent.HistoryToday -> s.copy(historyDate = today())
        is AppIntent.OpenWorkout -> s.push(AppRoute.WORKOUT_DETAIL).copy(selectedWorkout = i.workout)
        AppIntent.OpenBonusWorkout -> if (!s.bonusWorkoutUnlocked) s else s.push(AppRoute.WORKOUT_DETAIL).copy(selectedWorkout = SeedCatalog.workouts.first())
        is AppIntent.OpenExercise -> s.push(AppRoute.EXERCISE_DETAIL).copy(selectedExercise = i.exercise)
        AppIntent.StartPlayer -> s.copy(player = s.player?.copy(phase = PlayerPhase.EXERCISE, remainingSeconds = s.player.current.durationSeconds ?: 0))
        AppIntent.PausePlayer -> s.push(AppRoute.PAUSED)
        AppIntent.ResumePlayer -> s.copy(route = AppRoute.PLAYER, navigationStack = s.navigationStack.dropLast(1),
            player = s.player?.copy(pausedByBackground = false))
        AppIntent.CompleteStep, AppIntent.SkipRest -> advancePlayer(s)
        AppIntent.FinishWorkout -> if (s.player?.isBonusWorkout == true) s.copy(route = AppRoute.COMPLETE) else s.recordCompletion(Clock.System.now(), TimeZone.currentSystemDefault())
        AppIntent.ShowWorkoutResult -> s.copy(route = AppRoute.WORKOUT_RESULT)
        AppIntent.ExitPlayer -> s.copy(route = AppRoute.HOME, selectedTab = AppRoute.HOME, player = null, navigationStack = emptyList())
        is AppIntent.RecordInterstitialShown -> s.copy(lastInterstitialShownEpochMs = i.epochMs)
        AppIntent.GrantBonusWorkout -> s.copy(bonusWorkoutUnlocked = true)
        AppIntent.ToggleWater -> s.copy(waterDone = !s.waterDone); AppIntent.ToggleWalk -> s.copy(walkDone = !s.walkDone); AppIntent.ResetApp -> AppState()
    }

    fun beginSelectedWorkout() {
        if (restorationStatus.value != RestorationStatus.READY || !isAppVisible) return
        val workout = state.value.selectedWorkout ?: return
        mutableState.update {
            val bonus = it.bonusWorkoutUnlocked && AppRoute.PROFILE in it.navigationStack
            it.copy(bonusWorkoutUnlocked = if (bonus) false else it.bonusWorkoutUnlocked)
                .push(AppRoute.PLAYER)
                .copy(player = PlayerState(workout, isBonusWorkout = bonus))
        }
    }

    private fun advancePlayer(s: AppState): AppState {
        val p = s.player ?: return s
        if (p.phase == PlayerPhase.EXERCISE && p.current.restAfterSeconds > 0) return s.copy(player = p.copy(phase = PlayerPhase.REST, remainingSeconds = p.current.restAfterSeconds, completedExerciseIds = p.completedExerciseIds + p.current.exercise.id))
        val next = p.exerciseIndex + 1
        return if (next >= p.workout.exercises.size) s.copy(player = p.copy(phase = PlayerPhase.FINISHED)) else { val e = p.workout.exercises[next]; s.copy(player = p.copy(exerciseIndex = next, phase = PlayerPhase.EXERCISE, remainingSeconds = e.durationSeconds ?: 0)) }
    }

    private fun restartTimerIfNeeded(resetTick: Boolean = true) {
        if (resetTick) timerTick.reset()
        timerJob?.cancel(); val p = state.value.player ?: return
        if (!isAppVisible || state.value.route != AppRoute.PLAYER || p.phase !in setOf(PlayerPhase.EXERCISE, PlayerPhase.REST) || p.remainingSeconds <= 0) return
        timerJob = viewModelScope.launch {
            while (true) {
                timerTick.start()
                delay(timerTick.remaining)
                timerTick.reset()
                val current = state.value.player ?: return@launch
                if (state.value.route != AppRoute.PLAYER || current.phase !in setOf(PlayerPhase.EXERCISE, PlayerPhase.REST)) return@launch
                if (current.remainingSeconds > 1) mutableState.update { it.copy(player = current.copy(remainingSeconds = current.remainingSeconds - 1)) }
                else { mutableState.update { advancePlayer(it.copy(player = current.copy(remainingSeconds = 0))) }; restartTimerIfNeeded(); return@launch }
            }
        }
    }
}

// Keep the partial tick across in-process pauses; snapshots retain whole seconds.
internal class CountdownTick(private val timeSource: TimeSource = TimeSource.Monotonic) {
    var remaining: Duration = 1.seconds
        private set
    private var started: TimeMark? = null
    fun start() { started = timeSource.markNow() }
    fun pause() {
        started?.let { remaining = (remaining - it.elapsedNow()).coerceAtLeast(Duration.ZERO) }
        started = null
    }
    fun reset() { started = null; remaining = 1.seconds }
}

private fun AppState.pauseForBackground(): AppState {
    val active = player ?: return this
    if (route != AppRoute.PLAYER || active.phase !in setOf(PlayerPhase.EXERCISE, PlayerPhase.REST) || active.remainingSeconds <= 0) return this
    return push(AppRoute.PAUSED).copy(player = active.copy(pausedByBackground = true))
}

private fun AppState.push(destination: AppRoute) = copy(route = destination, navigationStack = navigationStack + route)
private fun AppState.pop() = copy(route = navigationStack.lastOrNull() ?: AppRoute.HOME, navigationStack = navigationStack.dropLast(1))
typealias AppStore = HomeFitViewModel
