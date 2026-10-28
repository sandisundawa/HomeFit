package com.homefit.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
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
    Box(Modifier.fillMaxSize().background(PageBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(92.dp).background(Accent, RoundedCornerShape(30.dp)), contentAlignment = Alignment.Center) { Icon(NavigationIcons.Workouts, contentDescription = null, Modifier.size(48.dp), tint = Color.White) }
            Spacer(Modifier.height(20.dp)); Text("homefit", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 34.sp)
            Text("MOVE WELL. FEEL STRONGER.", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
    Card(Modifier.glassCard(22.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) { Text("WORKOUT DAYS", color = TextSecondary, fontWeight = FontWeight.Bold); Text("07:00", fontSize = 40.sp, fontWeight = FontWeight.Bold); Text("You can change this later in Profile.", color = TextPrimary.copy(.6f)) }
    }
    WideButton("Use this reminder") { dispatch(AppIntent.ConfirmReminder) }
    TextButton({ dispatch(AppIntent.ConfirmReminder) }, Modifier.fillMaxWidth()) { Text("Not now") }
}

@Composable
fun CreatingPlanScreen(dispatch: (AppIntent) -> Unit) {
    LaunchedEffect(Unit) { delay(900); dispatch(AppIntent.CreatePlan) }
    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(30.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(120.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White, trackColor = Color.White.copy(.25f))
            }
            Spacer(Modifier.height(28.dp)); Text("Creating your plan", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Balancing movement, recovery, and your selected weekdays…", color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WeeklyScheduleScreen(state: AppState, dispatch: (AppIntent) -> Unit) = DetailFrame("Your 28-day schedule", "Workouts stay anchored to the weekdays you chose.", dispatch) {
    QuietBannerAd()
    state.program.forEach { day ->
        Row(Modifier.fillMaxWidth().glassCard(24.dp).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(if (day.workout != null) Accent else SurfaceMuted, CircleShape), contentAlignment = Alignment.Center) { Text(day.dayNumber.toString(), fontWeight = FontWeight.Bold, color = if (day.workout != null) Color.White else TextPrimary) }
            Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(day.workout?.name ?: "Recovery day", fontWeight = FontWeight.Bold); Text(if (day.workout != null) "${day.workout.durationMinutes} minutes" else "Walk, stretch, or rest", color = TextSecondary) }
            if (day.workout != null) TextButton({ dispatch(AppIntent.OpenWorkout(day.workout)) }, colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary)) { Text("View →") }
        }
    }
}

@Composable
fun WorkoutDetailScreen(workout: Workout?, dispatch: (AppIntent) -> Unit, onStart: () -> Unit) = Column(Modifier.fillMaxSize()) {
    Box(Modifier.weight(1f)) {
        DetailFrame(workout?.name ?: "Workout", "A clear preview before you begin.", dispatch) {
            if (workout == null) return@DetailFrame
            MetricStrip(listOf("${workout.durationMinutes}" to "minutes", "${workout.exercises.size}" to "moves", "LOW" to "impact"))
            Text("Workout flow", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            workout.exercises.forEachIndexed { index, item ->
                Row(Modifier.fillMaxWidth().glassCard(24.dp).clickable { dispatch(AppIntent.OpenExercise(item.exercise)) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) { Text("${index + 1}", fontWeight = FontWeight.Bold, color = Color.White) }
                    Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(item.exercise.name, fontWeight = FontWeight.SemiBold); Text(item.reps?.let { "$it reps" } ?: "${item.durationSeconds} sec", color = TextSecondary) }; Text("›")
                }
            }
        }
    }
    if (workout != null) FloatingWorkoutAction(onStart)
}

@Composable
fun ExerciseDetailScreen(exercise: Exercise?, dispatch: (AppIntent) -> Unit) = DetailFrame(exercise?.name ?: "Exercise", exercise?.category?.name?.replace('_', ' ') ?: "Movement guide", dispatch) {
    if (exercise == null) return@DetailFrame
    ExerciseDemo(exercise)
    Text(exercise.cue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text("How to do it", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
fun SettingsScreen(dispatch: (AppIntent) -> Unit, adsHost: AdsHost = NoOpAdsHost) = DetailFrame("Settings", "HomeFit stays local and works offline.", dispatch) {
    SettingCard("Units", "Metric")
    SettingCard("Exercise media", "Download on Wi-Fi")
    SettingCard("Sound & vibration", "Enabled")
    SettingCard("Privacy", "Your workout data stays on this device")
    if (adsHost.supportsPrivacyOptions) WideButton("Privacy choices") { adsHost.showPrivacyOptions {} }
    SettingCard("About HomeFit", "Version 0.1.0")
}

@Composable
fun PausedScreen(dispatch: (AppIntent) -> Unit, pausedByBackground: Boolean = false) = Box(Modifier.fillMaxSize().background(TextPrimary).verticalScroll(rememberScrollState()).padding(28.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Box(Modifier.size(112.dp).background(Accent, CircleShape), contentAlignment = Alignment.Center) { Text("Ⅱ", fontSize = 54.sp, color = Color.White) }
        Badge("WORKOUT PAUSED")
        Text("Take your time", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(if (pausedByBackground) "Paused while the app was away. Tap Resume to continue." else "Your place in the workout is safe.", color = Color.White.copy(.65f)); Spacer(Modifier.height(30.dp))
        PrimaryButton("Resume workout") { dispatch(AppIntent.ResumePlayer) }
        TextButton({ dispatch(AppIntent.ExitPlayer) }) { Text("End workout", color = Color.White) }
    }
}

@Composable
fun WorkoutResultScreen(state: AppState, dispatch: (AppIntent) -> Unit, adsHost: AdsHost = NoOpAdsHost) = DetailFrame("Session summary", "A little consistency compounds.", dispatch) {
    val workout = state.player?.workout
    MetricStrip(listOf("${workout?.exercises?.size ?: 0}" to "moves", "${workout?.durationMinutes ?: 0}" to "minutes", "100%" to "complete"))
    Text("How did it feel?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("Easy", "Good", "Hard").forEach { AssistChip(onClick = {}, label = { Text(it) }) } }
    InfoBox("Nice work. Your next scheduled session remains in its original slot.")
    WideButton("Back to today") {
        if (interstitialEligible(state)) adsHost.showSummaryInterstitial(state, { dispatch(AppIntent.RecordInterstitialShown(kotlin.time.Clock.System.now().toEpochMilliseconds())) }, { dispatch(AppIntent.ExitPlayer) })
        else dispatch(AppIntent.ExitPlayer)
    }
}

@Composable
internal fun DetailFrame(title: String, subtitle: String, dispatch: (AppIntent) -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TextButton({ dispatch(AppIntent.Back) }, shape = CircleShape,
            colors = ButtonDefaults.textButtonColors(containerColor = SurfaceMuted, contentColor = TextPrimary),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) { Text("←  Back") }
        ScreenTitle(title, subtitle)
        Spacer(Modifier.height(2.dp)); content(); Spacer(Modifier.height(20.dp))
    }
}

@Composable private fun SelectCard(title: String, subtitle: String, onClick: () -> Unit) = Card(onClick, Modifier.fillMaxWidth().glassCard(26.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), shape = RoundedCornerShape(26.dp)) {
    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = TextSecondary) }
        Box(Modifier.size(36.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) { Text("→", color = TextPrimary) }
    }
}
@Composable private fun CheckRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) = Row(Modifier.fillMaxWidth().glassCard(24.dp)
    .background(if (checked) Accent else Color.Transparent).toggleable(value = checked, role = Role.Checkbox, onValueChange = onChange).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
    Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = if (checked) Color.White else TextPrimary)
    Checkbox(checked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = Color.White, checkmarkColor = Accent))
}
@Composable private fun SettingCard(title: String, subtitle: String) = Column(Modifier.fillMaxWidth().glassCard(24.dp).padding(20.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = TextSecondary) }
@Composable private fun InfoBox(text: String) = Text(text, Modifier.fillMaxWidth().glassCard(24.dp).padding(20.dp), color = AccentText, lineHeight = 23.sp)
@Composable private fun WideButton(text: String, onClick: () -> Unit) = PrimaryButton(text, onClick)
@Composable private fun EmptyState(title: String, subtitle: String) = Column(Modifier.fillMaxWidth().glassCard(28.dp).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Icon(NavigationIcons.Progress, contentDescription = null, Modifier.size(48.dp), tint = AccentText)
    Text(title, fontWeight = FontWeight.Bold, fontSize = 19.sp, textAlign = TextAlign.Center)
    Text(subtitle, color = TextSecondary, textAlign = TextAlign.Center)
}
