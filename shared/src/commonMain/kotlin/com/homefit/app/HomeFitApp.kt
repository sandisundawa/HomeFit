package com.homefit.app

import androidx.compose.foundation.background
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
    HomeFitTheme {
        Surface(Modifier.fillMaxSize(), color = Cream) {
            when (state.route) {
                AppRoute.SPLASH -> SplashScreen { viewModel.dispatch(AppIntent.FinishSplash) }
                AppRoute.WELCOME -> Welcome { viewModel.dispatch(AppIntent.BeginOnboarding) }
                AppRoute.GOAL -> GoalScreen { viewModel.dispatch(AppIntent.ChooseGoal(it)) }
                AppRoute.FITNESS_LEVEL -> FitnessLevelScreen(viewModel::dispatch)
                AppRoute.FREQUENCY -> FrequencyScreen(viewModel::dispatch)
                AppRoute.DAYS -> DaysScreen(state.profile.workoutDays, viewModel::dispatch)
                AppRoute.PREFERENCES -> PreferencesScreen(state.profile.lowImpact, viewModel::dispatch)
                AppRoute.RESTRICTIONS -> RestrictionsScreen(viewModel::dispatch)
                AppRoute.REMINDER_SETUP -> ReminderSetupScreen(viewModel::dispatch)
                AppRoute.CREATING_PLAN -> CreatingPlanScreen(viewModel::dispatch)
                AppRoute.PLAN_READY -> PlanReady(state.profile.workoutDays.size) { viewModel.dispatch(AppIntent.EnterPlan) }
                AppRoute.WEEKLY_SCHEDULE -> WeeklyScheduleScreen(state, viewModel::dispatch)
                AppRoute.WORKOUT_DETAIL -> WorkoutDetailScreen(state.selectedWorkout, viewModel::dispatch, viewModel::beginSelectedWorkout)
                AppRoute.EXERCISE_DETAIL -> ExerciseDetailScreen(state.selectedExercise, viewModel::dispatch)
                AppRoute.WORKOUT_CATEGORY -> WorkoutCategoryScreen(viewModel::dispatch)
                AppRoute.HISTORY -> HistoryScreen(state, viewModel::dispatch)
                AppRoute.HABITS_DETAIL -> HabitsDetailScreen(state, viewModel::dispatch)
                AppRoute.EDIT_GOAL -> EditGoalScreen(state, viewModel::dispatch)
                AppRoute.SCHEDULE_SETTINGS -> ScheduleSettingsScreen(state, viewModel::dispatch)
                AppRoute.REMINDER_SETTINGS -> ReminderSettingsScreen(state, viewModel::dispatch)
                AppRoute.SETTINGS -> SettingsScreen(viewModel::dispatch)
                AppRoute.PLAYER -> PlayerScreen(state.player, viewModel::dispatch)
                AppRoute.PAUSED -> PausedScreen(viewModel::dispatch)
                AppRoute.COMPLETE -> CompleteScreen { viewModel.dispatch(AppIntent.ShowWorkoutResult) }
                AppRoute.WORKOUT_RESULT -> WorkoutResultScreen(state, viewModel::dispatch)
                else -> MainScaffold(state, viewModel::dispatch)
            }
        }
    }
}

@Composable private fun Welcome(onStart: () -> Unit) = Box(Modifier.fillMaxSize().padding(24.dp)) {
    Column(Modifier.align(Alignment.CenterStart)) {
        Badge("NO EQUIPMENT • BEGINNER FRIENDLY")
        Spacer(Modifier.height(24.dp))
        Text("Move well.\nFeel stronger.", fontSize = 48.sp, lineHeight = 51.sp, fontWeight = FontWeight.Black, color = Ink)
        Spacer(Modifier.height(16.dp))
        Text("A personal 28-day plan built around your week — in about 15 minutes a day.", fontSize = 18.sp, lineHeight = 26.sp, color = Moss)
        Spacer(Modifier.height(34.dp))
        PrimaryButton("Build my plan", onStart)
        Spacer(Modifier.height(14.dp))
        Text("Start gently. Progress at your pace.", color = Ink.copy(alpha = .55f))
    }
    Text("homefit / 01", Modifier.align(Alignment.TopEnd), fontWeight = FontWeight.Bold, color = Moss)
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
            Box(Modifier.weight(1f).aspectRatio(.72f).clip(RoundedCornerShape(18.dp)).background(if (day in selected) Moss else Color.White).clickable { dispatch(AppIntent.ToggleDay(day)) }, contentAlignment = Alignment.Center) {
                Text(label.take(1), fontWeight = FontWeight.Black, color = if (day in selected) Color.White else Ink)
            }
        }
    }
    Text("${selected.size} days each week", fontWeight = FontWeight.Bold, color = Moss)
    PrimaryButton("Continue", { dispatch(AppIntent.ConfirmDays) })
}

@Composable private fun PreferencesScreen(lowImpact: Boolean, dispatch: (AppIntent) -> Unit) = OnboardingFrame(3, "Make it work for your body", "You can change these preferences later.") {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Low-impact by default", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("No jumping in your generated plan", color = Ink.copy(.6f)) }
            Switch(lowImpact, { dispatch(AppIntent.SetLowImpact(it)) })
        }
    }
    InfoPanel("Your plan starts conservatively and builds over four weeks. You can always swap or skip a movement.")
    PrimaryButton("Continue", { dispatch(AppIntent.ConfirmPreferences) })
}

@Composable private fun PlanReady(days: Int, onEnter: () -> Unit) = Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(112.dp).background(Lime, CircleShape), contentAlignment = Alignment.Center) { Text("✓", fontSize = 52.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(28.dp)); Text("Your plan is ready", fontSize = 34.sp, fontWeight = FontWeight.Black)
        Text("28 days • $days workouts/week • no equipment", color = Moss, fontSize = 17.sp)
        Spacer(Modifier.height(30.dp)); PrimaryButton("See today's workout", onEnter)
    }
}

@Composable private fun MainScaffold(state: AppState, dispatch: (AppIntent) -> Unit) {
    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf(AppRoute.HOME to "Today", AppRoute.LIBRARY to "Workouts", AppRoute.PROGRESS to "Progress", AppRoute.PROFILE to "Profile").forEach { (route, label) ->
                    NavigationBarItem(route == state.selectedTab, { dispatch(AppIntent.SelectTab(route)) }, icon = { Text(if (route == state.selectedTab) "●" else "○") }, label = { Text(label) })
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text("DAY 01 / 28", color = Moss, fontWeight = FontWeight.Bold); Text("Good morning", fontSize = 30.sp, fontWeight = FontWeight.Black) }; Badge("WEEK 1") }
        Card(colors = CardDefaults.cardColors(containerColor = Ink), shape = RoundedCornerShape(28.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("TODAY'S SESSION", color = Lime, fontWeight = FontWeight.Bold); Text(today.name, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("${today.durationMinutes} min  •  ${today.exercises.size} movements  •  low impact", color = Color.White.copy(.7f))
                Button({ dispatch(AppIntent.OpenWorkout(today)) }, colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink), modifier = Modifier.fillMaxWidth()) { Text("Start workout", fontWeight = FontWeight.Black) }
            }
        }
        Row(Modifier.fillMaxWidth().clickable { dispatch(AppIntent.Navigate(AppRoute.WEEKLY_SCHEDULE)) }, horizontalArrangement = Arrangement.SpaceBetween) { Text("This week", fontSize = 22.sp, fontWeight = FontWeight.Black); Text("View schedule →", color = Moss) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { (1..7).forEach { DayDot(it, it in state.profile.workoutDays, it == 1) } }
        Row(Modifier.fillMaxWidth().clickable { dispatch(AppIntent.Navigate(AppRoute.HABITS_DETAIL)) }, horizontalArrangement = Arrangement.SpaceBetween) { Text("Healthy habits", fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Details →", color = Moss) }
        HabitRow("Water check", "Stay hydrated today", state.waterDone) { dispatch(AppIntent.ToggleWater) }
        HabitRow("Daily walk", "Aim for a little extra movement", state.walkDone) { dispatch(AppIntent.ToggleWalk) }
    }
}

@Composable private fun LibraryScreen(dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("Workout library", fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Beginner sessions for every kind of day.", color = Moss)
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.WORKOUT_CATEGORY)) }, Modifier.fillMaxWidth()) { Text("Browse by category") }
    SeedCatalog.workouts.take(8).forEach { workout ->
        Card(Modifier.fillMaxWidth().clickable { dispatch(AppIntent.OpenWorkout(workout)) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(if (workout.category == ExerciseCategory.CARDIO) Lime else Mist, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text(workout.slot.toString(), fontWeight = FontWeight.Black) }
                Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(workout.name, fontWeight = FontWeight.Bold); Text("Week ${workout.week} • ${workout.durationMinutes} min", color = Moss) }
                Text("→", fontSize = 22.sp)
            }
        }
    }
}

@Composable private fun ProgressScreen(state: AppState, dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
    Text("Your progress", fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Consistency beats intensity.", color = Moss)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { StatCard("${state.completedWorkoutIds.size}", "workouts", Modifier.weight(1f)); StatCard("${state.completedWorkoutIds.size * 14}", "minutes", Modifier.weight(1f)); StatCard("1", "day streak", Modifier.weight(1f)) }
    InfoPanel("Complete your first week to unlock a richer progress view. Weight tracking is optional and never used to judge a workout.")
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.HISTORY)) }, Modifier.fillMaxWidth()) { Text("View workout history") }
}

@Composable private fun ProfileScreen(state: AppState, dispatch: (AppIntent) -> Unit) = Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text("Profile", fontSize = 30.sp, fontWeight = FontWeight.Black)
    SettingRow("Goal", state.profile.goal.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() })
    SettingRow("Schedule", "${state.profile.workoutDays.size} days each week")
    SettingRow("Workout style", if (state.profile.lowImpact) "Low impact" else "Standard")
    SettingRow("Reminders", if (state.profile.reminderEnabled) "Enabled" else "Off")
    Button({ dispatch(AppIntent.Navigate(AppRoute.EDIT_GOAL)) }, Modifier.fillMaxWidth()) { Text("Edit goal") }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.SCHEDULE_SETTINGS)) }, Modifier.fillMaxWidth()) { Text("Schedule settings") }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.REMINDER_SETTINGS)) }, Modifier.fillMaxWidth()) { Text("Reminder settings") }
    OutlinedButton({ dispatch(AppIntent.Navigate(AppRoute.SETTINGS)) }, Modifier.fillMaxWidth()) { Text("General settings") }
    OutlinedButton({ dispatch(AppIntent.ResetApp) }, Modifier.fillMaxWidth()) { Text("Reset onboarding") }
}

@Composable private fun PlayerScreen(player: PlayerState?, dispatch: (AppIntent) -> Unit) {
    if (player == null) return
    if (player.phase == PlayerPhase.FINISHED) { LaunchedEffect(Unit) { dispatch(AppIntent.FinishWorkout) }; return }
    val item = player.current
    Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("✕", Modifier.clickable { dispatch(AppIntent.ExitPlayer) }, fontSize = 22.sp); Text("${player.exerciseIndex + 1} / ${player.workout.exercises.size}", fontWeight = FontWeight.Bold); Text("Pause", Modifier.clickable { dispatch(AppIntent.PausePlayer) }, color = Moss, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp)); LinearProgressIndicator(progress = { (player.exerciseIndex + 1f) / player.workout.exercises.size }, Modifier.fillMaxWidth(), color = Moss, trackColor = Mist)
        Spacer(Modifier.weight(.6f))
        Box(Modifier.size(230.dp).background(if (player.phase == PlayerPhase.REST) Lime else Mist, CircleShape), contentAlignment = Alignment.Center) { Text(if (player.phase == PlayerPhase.REST) "REST" else item.exercise.name.take(2).uppercase(), fontSize = 46.sp, fontWeight = FontWeight.Black, color = Ink) }
        Spacer(Modifier.height(28.dp)); Text(when (player.phase) { PlayerPhase.GET_READY -> "GET READY"; PlayerPhase.REST -> "BREATHE & RESET"; else -> item.section.name.replace('_', ' ') }, color = Moss, fontWeight = FontWeight.Bold)
        Text(if (player.phase == PlayerPhase.REST) "Rest" else item.exercise.name, fontSize = 32.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Text(if (player.phase == PlayerPhase.REST) "${player.remainingSeconds} sec" else item.reps?.let { "$it reps" } ?: "${player.remainingSeconds} sec", fontSize = 24.sp, color = Clay, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp)); Text(if (player.phase == PlayerPhase.REST) "Next: ${player.workout.exercises.getOrNull(player.exerciseIndex + 1)?.exercise?.name ?: "Finish"}" else item.exercise.cue, textAlign = TextAlign.Center, color = Ink.copy(.65f))
        Spacer(Modifier.weight(1f)); PrimaryButton(when (player.phase) { PlayerPhase.GET_READY -> "I'm ready"; PlayerPhase.REST -> "Skip rest"; else -> "Done" }) { dispatch(if (player.phase == PlayerPhase.GET_READY) AppIntent.StartPlayer else if (player.phase == PlayerPhase.REST) AppIntent.SkipRest else AppIntent.CompleteStep) }
    }
}

@Composable private fun CompleteScreen(onDone: () -> Unit) = Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("✦", fontSize = 70.sp, color = Clay); Text("Workout complete!", fontSize = 34.sp, fontWeight = FontWeight.Black); Text("You showed up. That's the win.", color = Moss); Spacer(Modifier.height(28.dp)); PrimaryButton("Back to today", onDone) } }

@Composable private fun OnboardingFrame(step: Int, title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) = Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
    Text("STEP $step OF 3", color = Moss, fontWeight = FontWeight.Black); LinearProgressIndicator(progress = { step / 3f }, Modifier.fillMaxWidth(), color = Moss, trackColor = Mist); Spacer(Modifier.height(8.dp)); Text(title, fontSize = 35.sp, lineHeight = 39.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Ink.copy(.62f), fontSize = 17.sp); Spacer(Modifier.height(4.dp)); content()
}

@Composable private fun PrimaryButton(text: String, onClick: () -> Unit) = Button(onClick, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Moss)) { Text(text, fontSize = 17.sp, fontWeight = FontWeight.Black) }
@Composable private fun Badge(text: String) = Box(Modifier.background(Lime, RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 7.dp)) { Text(text, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Ink) }
@Composable private fun ChoiceCard(title: String, subtitle: String, symbol: String, onClick: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) { Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Text(symbol, fontSize = 30.sp, color = Clay); Column(Modifier.padding(start = 16.dp)) { Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Ink.copy(.6f)) } } }
@Composable private fun InfoPanel(text: String) = Box(Modifier.fillMaxWidth().background(Mist, RoundedCornerShape(20.dp)).padding(18.dp)) { Text(text, lineHeight = 22.sp, color = Ink.copy(.75f)) }
@Composable private fun DayDot(day: Int, active: Boolean, today: Boolean) = Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(listOf("M","T","W","T","F","S","S")[day-1], fontSize = 11.sp, fontWeight = FontWeight.Bold); Box(Modifier.padding(top = 5.dp).size(35.dp).background(if (today) Moss else if (active) Lime else Mist, CircleShape), contentAlignment = Alignment.Center) { Text(if (active) "•" else "–", color = if (today) Color.White else Ink) } }
@Composable private fun HabitRow(title: String, subtitle: String, done: Boolean, onClick: () -> Unit) = Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)).clickable(onClick = onClick).padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(30.dp).background(if (done) Moss else Mist, CircleShape), contentAlignment = Alignment.Center) { Text(if (done) "✓" else "", color = Color.White) }; Column(Modifier.padding(start = 14.dp)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Ink.copy(.55f)) } }
@Composable private fun StatCard(value: String, label: String, modifier: Modifier) = Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(14.dp)) { Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black); Text(label, fontSize = 12.sp, color = Moss) } }
@Composable private fun SettingRow(label: String, value: String) = Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, fontWeight = FontWeight.Bold); Text(value, color = Moss) }
