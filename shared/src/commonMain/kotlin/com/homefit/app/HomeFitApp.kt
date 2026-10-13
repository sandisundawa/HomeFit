package com.homefit.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homefit.core.data.SeedCatalog
import com.homefit.core.model.*
import com.homefit.feature.app.*

@Composable
fun HomeFitApp(viewModel: HomeFitViewModel) {
    val state by viewModel.state.collectAsState()
    HomeFitContent(state, viewModel::dispatch, viewModel::beginSelectedWorkout)
}

@Composable
internal fun HomeFitContent(state: AppState, dispatch: (AppIntent) -> Unit, onStartWorkout: () -> Unit) {
    HomeFitTheme {
        Surface(Modifier.fillMaxSize().safeDrawingPadding(), color = PageBackground) {
            when (state.route) {
                AppRoute.SPLASH -> SplashScreen { dispatch(AppIntent.FinishSplash) }
                AppRoute.WELCOME -> Welcome { dispatch(AppIntent.BeginOnboarding) }
                AppRoute.GOAL -> GoalScreen { dispatch(AppIntent.ChooseGoal(it)) }
                AppRoute.FITNESS_LEVEL -> FitnessLevelScreen(dispatch)
                AppRoute.FREQUENCY -> FrequencyScreen(dispatch)
                AppRoute.DAYS -> DaysScreen(state.profile.workoutDays, dispatch)
                AppRoute.PREFERENCES -> PreferencesScreen(state.profile.lowImpact, dispatch)
                AppRoute.RESTRICTIONS -> RestrictionsScreen(dispatch)
                AppRoute.REMINDER_SETUP -> ReminderSetupScreen(dispatch)
                AppRoute.CREATING_PLAN -> CreatingPlanScreen(dispatch)
                AppRoute.PLAN_READY -> PlanReady(state.profile.workoutDays.size) { dispatch(AppIntent.EnterPlan) }
                AppRoute.WEEKLY_SCHEDULE -> WeeklyScheduleScreen(state, dispatch)
                AppRoute.WORKOUT_DETAIL -> WorkoutDetailScreen(state.selectedWorkout, dispatch, onStartWorkout)
                AppRoute.EXERCISE_DETAIL -> ExerciseDetailScreen(state.selectedExercise, dispatch)
                AppRoute.WORKOUT_CATEGORY -> WorkoutCategoryScreen(dispatch)
                AppRoute.HISTORY -> HistoryScreen(state, dispatch)
                AppRoute.HABITS_DETAIL -> HabitsDetailScreen(state, dispatch)
                AppRoute.EDIT_GOAL -> EditGoalScreen(state, dispatch)
                AppRoute.SCHEDULE_SETTINGS -> ScheduleSettingsScreen(state, dispatch)
                AppRoute.REMINDER_SETTINGS -> ReminderSettingsScreen(state, dispatch)
                AppRoute.SETTINGS -> SettingsScreen(dispatch)
                AppRoute.PLAYER -> PlayerScreen(state.player, dispatch)
                AppRoute.PAUSED -> PausedScreen(dispatch)
                AppRoute.COMPLETE -> CompleteScreen { dispatch(AppIntent.ShowWorkoutResult) }
                AppRoute.WORKOUT_RESULT -> WorkoutResultScreen(state, dispatch)
                else -> MainScaffold(state, dispatch)
            }
        }
    }
}

@Composable private fun Welcome(onStart: () -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(48.dp).background(Accent, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(NavigationIcons.Workouts, contentDescription = null, tint = TextPrimary)
        }
        Text("homefit", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
    Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(32.dp)).background(SurfaceMuted), contentAlignment = Alignment.Center) {
        Box(Modifier.size(128.dp).background(AccentSoft, CircleShape), contentAlignment = Alignment.Center) {
            Icon(NavigationIcons.Workouts, contentDescription = null, Modifier.size(64.dp), tint = AccentText)
        }
    }
    Column {
        Badge("NO EQUIPMENT • BEGINNER FRIENDLY")
        Spacer(Modifier.height(24.dp))
        Text("Move well.\nFeel stronger.", fontSize = 38.sp, lineHeight = 43.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(16.dp))
        Text("A personal 28-day plan built around your week — in about 15 minutes a day.", fontSize = 18.sp, lineHeight = 26.sp, color = TextSecondary)
        Spacer(Modifier.height(34.dp))
        PrimaryButton("Build my plan", onStart)
        Spacer(Modifier.height(14.dp))
        Text("Start gently. Progress at your pace.", color = TextPrimary.copy(alpha = .55f))
    }
}

@Composable private fun GoalScreen(onChoose: (FitnessGoal) -> Unit) = OnboardingFrame(1, "What brings you here?", "We'll shape your plan around one main goal.") {
    ChoiceCard("Lose weight", "Build a consistent movement habit", "↘") { onChoose(FitnessGoal.WEIGHT_LOSS) }
    ChoiceCard("Feel fitter", "Improve everyday energy and mobility", "✦") { onChoose(FitnessGoal.GENERAL_FITNESS) }
    ChoiceCard("Get stronger", "Build a solid bodyweight foundation", "+") { onChoose(FitnessGoal.BUILD_STRENGTH) }
}

@Composable private fun DaysScreen(selected: Set<Int>, dispatch: (AppIntent) -> Unit) = OnboardingFrame(2, "Choose your workout days", "Pick 3–5 days. Missed workouts stay missed; your schedule won't shift.") {
    val names = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        names.forEachIndexed { index, label ->
            val day = index + 1
            Box(Modifier.weight(1f).heightIn(min = 64.dp).clip(RoundedCornerShape(22.dp)).background(if (day in selected) Accent else SurfaceMuted).toggleable(value = day in selected, role = Role.Checkbox) { dispatch(AppIntent.ToggleDay(day)) }, contentAlignment = Alignment.Center) {
                Text(label.take(1), fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
    Text("${selected.size} days each week", fontWeight = FontWeight.Bold, color = TextSecondary)
    PrimaryButton("Continue", { dispatch(AppIntent.ConfirmDays) })
}

@Composable private fun PreferencesScreen(lowImpact: Boolean, dispatch: (AppIntent) -> Unit) = OnboardingFrame(3, "Make it work for your body", "You can change these preferences later.") {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceMuted), shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Low-impact by default", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("No jumping in your generated plan", color = TextPrimary.copy(.6f)) }
            Switch(lowImpact, { dispatch(AppIntent.SetLowImpact(it)) })
        }
    }
    InfoPanel("Your plan starts conservatively and builds over four weeks. You can always swap or skip a movement.")
    PrimaryButton("Continue", { dispatch(AppIntent.ConfirmPreferences) })
}

@Composable private fun PlanReady(days: Int, onEnter: () -> Unit) = Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(112.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) { Text("✓", fontSize = 52.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(28.dp)); Text("Your plan is ready", fontSize = 34.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("28 days • $days workouts/week • no equipment", color = TextSecondary, fontSize = 17.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(30.dp)); PrimaryButton("See today's workout", onEnter)
    }
}

@Composable private fun MainScaffold(state: AppState, dispatch: (AppIntent) -> Unit) {
    Scaffold(
        containerColor = PageBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(32.dp)).background(SurfaceMuted).padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(AppRoute.HOME to "Today", AppRoute.LIBRARY to "Workouts", AppRoute.PROGRESS to "Progress", AppRoute.PROFILE to "Profile").forEach { (route, label) ->
                    val selected = route == state.selectedTab
                    Surface(
                        onClick = { dispatch(AppIntent.SelectTab(route)) },
                        modifier = Modifier.weight(1f).semantics { this.selected = selected },
                        shape = RoundedCornerShape(26.dp),
                        color = if (selected) Accent else Color.Transparent,
                        contentColor = if (selected) TextPrimary else TextSecondary,
                    ) {
                        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = NavigationIcons.forRoute(route),
                                contentDescription = null, // The visible tab label supplies accessibility text.
                                modifier = Modifier.size(24.dp),
                            )
                            Text(label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (state.route) {
                AppRoute.HOME -> HomeScreen(state, dispatch)
                AppRoute.LIBRARY -> LibraryScreen(dispatch)
                AppRoute.PROGRESS -> ProgressScreen(state, dispatch)
                AppRoute.PROFILE -> ProfileScreen(state, dispatch)
                else -> HomeScreen(state, dispatch)
            }
        }
    }
}

@Composable private fun HomeScreen(state: AppState, dispatch: (AppIntent) -> Unit) {
    val today = state.program.firstOrNull { it.workout != null }?.workout ?: SeedCatalog.workouts.first()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(48.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) {
                Icon(NavigationIcons.Today, contentDescription = null, tint = TextPrimary)
            }
            Column(Modifier.weight(1f)) { Text("Good morning", color = TextSecondary, fontSize = 14.sp); Text("HomeFit", fontSize = 23.sp, fontWeight = FontWeight.Bold) }
            Badge("WEEK 1")
        }
        SectionHeading("This week", "Schedule →") { dispatch(AppIntent.Navigate(AppRoute.WEEKLY_SCHEDULE)) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { (1..7).forEach { DayDot(it, it in state.profile.workoutDays, it == 1) } }
        Text("Today's planned workout", style = MaterialTheme.typography.titleLarge)
        WorkoutCard(today, "Start workout  →") { dispatch(AppIntent.OpenWorkout(today)) }
        SectionHeading("Healthy habits", "Details →") { dispatch(AppIntent.Navigate(AppRoute.HABITS_DETAIL)) }
        HabitRow("Water check", "Stay hydrated today", state.waterDone) { dispatch(AppIntent.ToggleWater) }
        HabitRow("Daily walk", "Aim for a little extra movement", state.walkDone) { dispatch(AppIntent.ToggleWalk) }
    }
}

@Composable private fun LibraryScreen(dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    ScreenTitle("Workouts", "Beginner sessions for every kind of day.")
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.WORKOUT_CATEGORY)) }, Modifier.fillMaxWidth()) { Text("Browse by category") }
    SeedCatalog.workouts.take(8).forEach { workout ->
        WorkoutCard(workout, "View workout · Week ${workout.week}  →") { dispatch(AppIntent.OpenWorkout(workout)) }
    }
}

@Composable private fun ProgressScreen(state: AppState, dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
    ScreenTitle("Your progress", "Consistency beats intensity.")
    Box(Modifier.fillMaxWidth().height(140.dp).background(AccentSoft, RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
        Icon(NavigationIcons.Progress, contentDescription = null, Modifier.size(72.dp), tint = AccentText)
    }
    MetricStrip(listOf("${state.completedWorkoutIds.size}" to "workouts", "${state.completedWorkoutIds.size * 14}" to "minutes", "1" to "day streak"))
    InfoPanel("Complete your first week to unlock a richer progress view. Weight tracking is optional and never used to judge a workout.")
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.HISTORY)) }, Modifier.fillMaxWidth()) { Text("View workout history") }
}

@Composable private fun ProfileScreen(state: AppState, dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    ScreenTitle("Profile", "Your plan. Your pace.")
    Row(Modifier.fillMaxWidth().background(AccentSoft, RoundedCornerShape(28.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.size(56.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) { Icon(NavigationIcons.Profile, contentDescription = null, tint = TextPrimary) }
        Column { Text("Your HomeFit plan", style = MaterialTheme.typography.titleMedium); Text("28 days of movement", color = AccentText) }
    }
    SettingRow("Goal", state.profile.goal.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() })
    SettingRow("Schedule", "${state.profile.workoutDays.size} days each week")
    SettingRow("Workout style", if (state.profile.lowImpact) "Low impact" else "Standard")
    SettingRow("Reminders", if (state.profile.reminderEnabled) "Enabled" else "Off")
    PrimaryButton("Edit goal") { dispatch(AppIntent.Navigate(AppRoute.EDIT_GOAL)) }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.SCHEDULE_SETTINGS)) }, Modifier.fillMaxWidth()) { Text("Schedule settings") }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.REMINDER_SETTINGS)) }, Modifier.fillMaxWidth()) { Text("Reminder settings") }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.SETTINGS)) }, Modifier.fillMaxWidth()) { Text("General settings") }
    OutlinedButton({ dispatch(AppIntent.ResetApp) }, Modifier.fillMaxWidth()) { Text("Reset onboarding") }
}

@Composable private fun PlayerScreen(player: PlayerState?, dispatch: (AppIntent) -> Unit) {
    if (player == null) return
    if (player.phase == PlayerPhase.FINISHED) { LaunchedEffect(Unit) { dispatch(AppIntent.FinishWorkout) }; return }
    val item = player.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton({ dispatch(AppIntent.ExitPlayer) }, colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)) { Text("✕  Exit") }
            Badge("${player.exerciseIndex + 1} / ${player.workout.exercises.size}")
            TextButton({ dispatch(AppIntent.PausePlayer) }, colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)) { Text("Pause") }
        }
        LinearProgressIndicator(progress = { (player.exerciseIndex + 1f) / player.workout.exercises.size }, Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = Accent, trackColor = SurfaceMuted)
        if (player.phase == PlayerPhase.REST) {
            Box(Modifier.size(230.dp).background(AccentSoft, CircleShape), contentAlignment = Alignment.Center) {
                Text("REST", fontSize = 46.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        } else {
            ExerciseAnimation(item.exercise, playing = player.phase == PlayerPhase.EXERCISE, modifier = Modifier.size(230.dp))
        }
        Badge(when (player.phase) { PlayerPhase.GET_READY -> "GET READY"; PlayerPhase.REST -> "BREATHE & RESET"; else -> item.section.name.replace('_', ' ') })
        Text(if (player.phase == PlayerPhase.REST) "Rest" else item.exercise.name, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(if (player.phase == PlayerPhase.REST) "${player.remainingSeconds} sec" else item.reps?.let { "$it reps" } ?: "${player.remainingSeconds} sec", fontSize = 36.sp, color = AccentText, fontWeight = FontWeight.Bold)
        Text(if (player.phase == PlayerPhase.REST) "Next: ${player.workout.exercises.getOrNull(player.exerciseIndex + 1)?.exercise?.name ?: "Finish"}" else item.exercise.cue, textAlign = TextAlign.Center, color = TextSecondary)
        PrimaryButton(when (player.phase) { PlayerPhase.GET_READY -> "I'm ready"; PlayerPhase.REST -> "Skip rest"; else -> "Done" }) { dispatch(if (player.phase == PlayerPhase.GET_READY) AppIntent.StartPlayer else if (player.phase == PlayerPhase.REST) AppIntent.SkipRest else AppIntent.CompleteStep) }
    }
}

@Composable private fun CompleteScreen(onDone: () -> Unit) = Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(Modifier.size(120.dp).background(AccentSoft, CircleShape), contentAlignment = Alignment.Center) { Text("✓", fontSize = 60.sp, color = AccentText) }
        Text("Workout complete!", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text("You showed up. That's the win.", color = TextSecondary)
        PrimaryButton("Back to today", onDone)
    }
}

@Composable private fun OnboardingFrame(step: Int, title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
    Badge("YOUR PLAN · STEP $step OF 3")
    LinearProgressIndicator(progress = { step / 3f }, Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = Accent, trackColor = SurfaceMuted)
    Spacer(Modifier.height(8.dp))
    ScreenTitle(title, subtitle)
    Spacer(Modifier.height(4.dp)); content()
}

@Composable private fun ChoiceCard(title: String, subtitle: String, symbol: String, onClick: () -> Unit) = Card(onClick, Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceMuted), shape = RoundedCornerShape(26.dp)) {
    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.size(48.dp).background(AccentSoft, CircleShape), contentAlignment = Alignment.Center) { Text(symbol, fontSize = 26.sp, color = AccentText) }
        Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = TextSecondary) }
        Text("›", fontSize = 24.sp)
    }
}
@Composable private fun InfoPanel(text: String) = Box(Modifier.fillMaxWidth().background(SurfaceMuted, RoundedCornerShape(20.dp)).padding(18.dp)) { Text(text, lineHeight = 22.sp, color = TextPrimary.copy(.75f)) }
@Composable private fun DayDot(day: Int, active: Boolean, today: Boolean) = Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(listOf("M","T","W","T","F","S","S")[day-1], fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Box(Modifier.size(34.dp).border(2.dp, if (today) Accent else Color.Transparent, CircleShape).padding(4.dp)
        .background(if (today) Accent else if (active) TextPrimary else SurfaceMuted, CircleShape), contentAlignment = Alignment.Center) {
        Text(if (active) "•" else "–", color = if (active && !today) Color.White else TextPrimary)
    }
}
@Composable private fun HabitRow(title: String, subtitle: String, done: Boolean, onClick: () -> Unit) = Card(onClick, Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceMuted), shape = RoundedCornerShape(24.dp)) {
    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).background(if (done) TextPrimary else Color.White, CircleShape), contentAlignment = Alignment.Center) { Text(if (done) "✓" else "○", color = if (done) Color.White else TextSecondary) }
        Column(Modifier.padding(start = 14.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = TextSecondary, fontSize = 13.sp) }
    }
}
@Composable private fun SettingRow(label: String, value: String) = Row(Modifier.fillMaxWidth().background(SurfaceMuted, RoundedCornerShape(24.dp)).padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { Text(label, Modifier.weight(1f), fontWeight = FontWeight.SemiBold); Text(value, Modifier.weight(1f), color = TextSecondary, textAlign = TextAlign.End) }
