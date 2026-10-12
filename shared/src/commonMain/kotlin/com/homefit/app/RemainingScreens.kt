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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) { delay(700); onFinished() }
    Box(Modifier.fillMaxSize().background(Ink), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(92.dp).background(Lime, CircleShape), contentAlignment = Alignment.Center) { Text("H", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Ink) }
            Spacer(Modifier.height(20.dp)); Text("HOMEFIT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("MOVE WELL. FEEL STRONGER.", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FitnessLevelScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Fitness level", "Choose the starting point that feels honest today.", dispatch) {
    SelectCard("Beginner", "I'm starting or returning after a break") { dispatch(AppIntent.ChooseLevel(FitnessLevel.BEGINNER)) }
    SelectCard("Intermediate", "I already exercise consistently") { dispatch(AppIntent.ChooseLevel(FitnessLevel.INTERMEDIATE)) }
}

@Composable
fun FrequencyScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Weekly rhythm", "How often would you like to train? You can choose exact days next.", dispatch) {
    (3..5).forEach { days -> SelectCard("$days days per week", when (days) { 3 -> "Gentle and sustainable"; 4 -> "Recommended balance"; else -> "More frequent, shorter sessions" }) { dispatch(AppIntent.ChooseFrequency(days)) } }
}

@Composable
fun RestrictionsScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Anything to avoid?", "These preferences help us choose safer alternatives. This is not medical advice.", dispatch) {
    var knees by remember { mutableStateOf(false) }; var wrists by remember { mutableStateOf(false) }; var floor by remember { mutableStateOf(false) }
    CheckRow("Deep knee bending", knees) { knees = it }
    CheckRow("Wrist-supported movements", wrists) { wrists = it }
    CheckRow("Floor exercises", floor) { floor = it }
    InfoBox("If pain, dizziness, or unusual shortness of breath occurs, stop the workout and seek appropriate guidance.")
    WideButton("Continue") { dispatch(AppIntent.ConfirmRestrictions) }
}

@Composable
fun ReminderSetupScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Set a gentle reminder", "Choose when HomeFit should nudge you on workout days.", dispatch) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) { Text("WORKOUT DAYS", color = Moss, fontWeight = FontWeight.Bold); Text("07:00", fontSize = 40.sp, fontWeight = FontWeight.Black); Text("You can change this later in Profile.", color = Ink.copy(.6f)) }
    }
    WideButton("Use this reminder") { dispatch(AppIntent.ConfirmReminder) }
    TextButton({ dispatch(AppIntent.ConfirmReminder) }, Modifier.fillMaxWidth()) { Text("Not now") }
}

@Composable
fun CreatingPlanScreen(dispatch: (AppIntent) -> Unit) {
    LaunchedEffect(Unit) { delay(900); dispatch(AppIntent.CreatePlan) }
    Box(Modifier.fillMaxSize().padding(30.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Moss, trackColor = Mist)
            Spacer(Modifier.height(28.dp)); Text("Creating your plan", fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Balancing movement, recovery, and your selected weekdays…", color = Moss, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WeeklyScheduleScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Your 28-day schedule", "Workouts stay anchored to the weekdays you chose.", dispatch) {
    state.program.take(14).forEach { day ->
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(if (day.workout != null) Lime else Mist, CircleShape), contentAlignment = Alignment.Center) { Text(day.dayNumber.toString(), fontWeight = FontWeight.Black) }
            Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(day.workout?.name ?: "Recovery day", fontWeight = FontWeight.Bold); Text(if (day.workout != null) "${day.workout.durationMinutes} minutes" else "Walk, stretch, or rest", color = Moss) }
            if (day.workout != null) Text("→", Modifier.clickable { dispatch(AppIntent.OpenWorkout(day.workout)) }, fontSize = 22.sp)
        }
    }
}

@Composable
fun WorkoutDetailScreen(workout: Workout?, dispatch: (AppIntent) -> Unit, onStart: () -> Unit) = DetailFrame(workout?.name ?: "Workout", "A clear preview before you begin.", dispatch) {
    if (workout == null) return@DetailFrame
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("${workout.durationMinutes}", "minutes", Modifier.weight(1f)); Metric("${workout.exercises.size}", "moves", Modifier.weight(1f)); Metric("LOW", "impact", Modifier.weight(1f)) }
    Text("Workout flow", fontSize = 21.sp, fontWeight = FontWeight.Black)
    workout.exercises.forEachIndexed { index, item ->
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).clickable { dispatch(AppIntent.OpenExercise(item.exercise)) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${index + 1}", fontWeight = FontWeight.Black, color = Moss); Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(item.exercise.name, fontWeight = FontWeight.Bold); Text(item.reps?.let { "$it reps" } ?: "${item.durationSeconds} sec", color = Ink.copy(.55f)) }; Text("›")
        }
    }
    WideButton("Start workout", onStart)
}

@Composable
fun ExerciseDetailScreen(exercise: Exercise?, dispatch: (AppIntent) -> Unit) = DetailFrame(exercise?.name ?: "Exercise", exercise?.category?.name?.replace('_', ' ') ?: "Movement guide", dispatch) {
    if (exercise == null) return@DetailFrame
    ExerciseDemo(exercise)
    Text(exercise.cue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text("How to do it", fontSize = 20.sp, fontWeight = FontWeight.Black)
    exercise.instructions.forEachIndexed { i, text -> Text("${i + 1}. $text", lineHeight = 22.sp) }
    InfoBox("Move through a comfortable range. Stop if you feel sharp pain.")
}

@Composable
fun WorkoutCategoryScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Browse by category", "Simple categories designed for beginners.", dispatch) {
    ExerciseCategory.entries.filter { it !in listOf(ExerciseCategory.WARM_UP) }.forEach { category ->
        val count = SeedCatalog.workouts.count { it.category == category }
        SelectCard(category.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "$count available workouts") {
            SeedCatalog.workouts.firstOrNull { it.category == category }?.let { dispatch(AppIntent.OpenWorkout(it)) }
        }
    }
}

@Composable
fun HistoryScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Workout history", "Your completed sessions appear here.", dispatch) {
    if (state.completedWorkoutIds.isEmpty()) EmptyState("No completed workouts yet", "Your first finished session will show here.")
    else state.completedWorkoutIds.forEach { id ->
        val workout = SeedCatalog.workouts.firstOrNull { it.id == id }
        SettingCard(workout?.name ?: "Workout", "Completed • ${workout?.durationMinutes ?: 0} minutes")
    }
}

@Composable
fun HabitsDetailScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Healthy habits", "Small actions that support your workout routine.", dispatch) {
    CheckRow("Drink enough water", state.waterDone) { dispatch(AppIntent.ToggleWater) }
    CheckRow("Take a daily walk", state.walkDone) { dispatch(AppIntent.ToggleWalk) }
    SettingCard("Sleep routine", "Aim for a consistent bedtime")
    SettingCard("Movement breaks", "Stand and move during long sitting periods")
    InfoBox("Workout completion is tracked separately and never duplicated as a habit.")
}

@Composable
fun EditGoalScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Edit your goal", "Changing this does not rewrite an active 28-day plan.", dispatch) {
    FitnessGoal.entries.forEach { goal ->
        val selected = goal == state.profile.goal
        SelectCard((if (selected) "✓ " else "") + goal.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, if (selected) "Current goal" else "Use for your next plan") { dispatch(AppIntent.ChooseGoal(goal)); dispatch(AppIntent.Navigate(AppRoute.PROFILE)) }
    }
}

@Composable
fun ScheduleSettingsScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Workout schedule", "Your active plan remains deterministic.", dispatch) {
    val names = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    names.forEachIndexed { index, name -> CheckRow(name, index + 1 in state.profile.workoutDays) { dispatch(AppIntent.ToggleDay(index + 1)) } }
    WideButton("Save schedule") { dispatch(AppIntent.Navigate(AppRoute.PROFILE)) }
}

@Composable
fun ReminderSettingsScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Reminder settings", "Notifications are scheduled only on workout days.", dispatch) {
    CheckRow("Workout reminders", state.profile.reminderEnabled) { }
    SettingCard("Time", "07:00")
    SettingCard("Message", "Your 15-minute session is ready")
    WideButton("Save reminder") { dispatch(AppIntent.Navigate(AppRoute.PROFILE)) }
}

@Composable
fun SettingsScreen(dispatch: (AppIntent) -> Unit) = DetailFrame("Settings", "HomeFit stays local and works offline.", dispatch) {
    SettingCard("Units", "Metric")
    SettingCard("Exercise media", "Download on Wi-Fi")
    SettingCard("Sound & vibration", "Enabled")
    SettingCard("Privacy", "Your workout data stays on this device")
    SettingCard("About HomeFit", "Version 0.1.0")
}

@Composable
fun PausedScreen(dispatch: (AppIntent) -> Unit) = Box(Modifier.fillMaxSize().background(Ink).padding(28.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("PAUSED", color = Lime, fontWeight = FontWeight.Black); Text("Take your time", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
        Text("Your place in the workout is safe.", color = Color.White.copy(.65f)); Spacer(Modifier.height(30.dp))
        Button({ dispatch(AppIntent.ResumePlayer) }, Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)) { Text("Resume workout", fontWeight = FontWeight.Black) }
        TextButton({ dispatch(AppIntent.ExitPlayer) }) { Text("End workout", color = Color.White) }
    }
}

@Composable
fun WorkoutResultScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Session summary", "A little consistency compounds.", dispatch) {
    val workout = state.player?.workout
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("${workout?.exercises?.size ?: 0}", "moves", Modifier.weight(1f)); Metric("${workout?.durationMinutes ?: 0}", "minutes", Modifier.weight(1f)); Metric("100%", "complete", Modifier.weight(1f)) }
    Text("How did it feel?", fontSize = 22.sp, fontWeight = FontWeight.Black)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("Easy", "Good", "Hard").forEach { AssistChip(onClick = {}, label = { Text(it) }) } }
    InfoBox("Nice work. Your next scheduled session remains in its original slot.")
    WideButton("Back to today") { dispatch(AppIntent.ExitPlayer) }
}

@Composable
private fun DetailFrame(title: String, subtitle: String, dispatch: (AppIntent) -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("← Back", Modifier.clickable { dispatch(AppIntent.Back) }, color = Moss, fontWeight = FontWeight.Bold)
        Text(title, fontSize = 31.sp, lineHeight = 35.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = Ink.copy(.62f), lineHeight = 22.sp)
        Spacer(Modifier.height(2.dp)); content(); Spacer(Modifier.height(20.dp))
    }
}

@Composable private fun SelectCard(title: String, subtitle: String, onClick: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text(subtitle, color = Ink.copy(.58f)) } }
@Composable private fun CheckRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) = Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold); Checkbox(checked, onCheckedChange = onChange) }
@Composable private fun SettingCard(title: String, subtitle: String) = Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(17.dp)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Moss) }
@Composable private fun InfoBox(text: String) = Text(text, Modifier.fillMaxWidth().background(Mist, RoundedCornerShape(18.dp)).padding(17.dp), color = Ink.copy(.72f), lineHeight = 21.sp)
@Composable private fun WideButton(text: String, onClick: () -> Unit) = Button(onClick, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) { Text(text, fontWeight = FontWeight.Black) }
@Composable private fun Metric(value: String, label: String, modifier: Modifier) = Column(modifier.background(Color.White, RoundedCornerShape(18.dp)).padding(14.dp)) { Text(value, fontSize = 25.sp, fontWeight = FontWeight.Black); Text(label, fontSize = 12.sp, color = Moss) }
@Composable private fun EmptyState(title: String, subtitle: String) = Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("○", fontSize = 42.sp, color = Moss); Text(title, fontWeight = FontWeight.Black, fontSize = 19.sp); Text(subtitle, color = Ink.copy(.6f), textAlign = TextAlign.Center) }
