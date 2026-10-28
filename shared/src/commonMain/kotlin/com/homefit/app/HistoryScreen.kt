package com.homefit.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homefit.core.data.SeedCatalog
import com.homefit.feature.app.*
import kotlinx.datetime.LocalDate

@Composable
fun HistoryScreen(state: AppState, dispatch: (AppIntent) -> Unit) {
    val currentDate = today()
    val selected = state.historyDate ?: currentDate
    val summary = remember(state.workoutHistory, selected) { historySummary(state.workoutHistory, selected) }
    val dailyCounts = remember(state.workoutHistory) { state.workoutHistory.groupingBy { it.localDate }.eachCount() }
    val sessions = remember(state.workoutHistory, selected) {
        state.workoutHistory.filter { it.localDate == selected }.sortedByDescending { it.completedAtEpochMs }
    }
    DetailFrame("Workout history", "Every active day counts. Rest days belong here too.", dispatch) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Week of ${summary.dates.first()}", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            TextButton({ dispatch(AppIntent.HistoryToday) }) { Text("Today") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HistoryMetric("${summary.activeDays}", "Active days", Modifier.weight(1f))
            HistoryMetric("${summary.sessions}", "Sessions", Modifier.weight(1f))
        }
        HistoryPanel {
            Text("Sessions per day", fontWeight = FontWeight.Bold)
            WeeklyHistoryChart(summary, selected) { dispatch(AppIntent.SelectHistoryDate(it)) }
        }
        HistoryPanel {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton({ dispatch(AppIntent.ChangeHistoryMonth(-1)) }, Modifier.semantics { contentDescription = "Previous month" }) { Text("‹", fontSize = 28.sp) }
                Text("${selected.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selected.year}",
                    Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                IconButton({ dispatch(AppIntent.ChangeHistoryMonth(1)) }, Modifier.semantics { contentDescription = "Next month" }) { Text("›", fontSize = 28.sp) }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                    Text(it, Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Moss)
                }
            }
            historyMonth(selected).chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) Spacer(Modifier.weight(1f).height(56.dp))
                        else CalendarDay(date, selected, currentDate, dailyCounts[date] ?: 0, Modifier.weight(1f)) {
                            dispatch(AppIntent.SelectHistoryDate(date))
                        }
                    }
                }
            }
            Text("• Workout day    Underline = today", fontSize = 12.sp, color = Moss)
        }
        Text("Sessions · $selected", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if (sessions.isEmpty()) HistoryPanel {
            Text("No completed workouts on this day.")
            Text("Finished sessions will appear here.", color = Moss)
        }
        sessions.forEach { session -> HistoryPanel {
            Text(session.workoutName, fontWeight = FontWeight.Bold)
            Text("Completed at ${session.localTime}", color = Moss)
        } }
        if (state.earlierWorkoutIds.isNotEmpty()) {
            Text("Earlier workouts — dates unavailable", fontWeight = FontWeight.Bold)
            Text("Saved before dated history. Not included in the chart or weekly counts.", color = Moss)
            state.earlierWorkoutIds.forEach { id -> HistoryPanel {
                Text(SeedCatalog.workouts.firstOrNull { it.id == id }?.name ?: "Workout", fontWeight = FontWeight.Bold)
                Text("Previously completed", color = Moss)
            } }
        }
    }
}

@Composable
private fun CalendarDay(date: LocalDate, selected: LocalDate, today: LocalDate, count: Int, modifier: Modifier, onClick: () -> Unit) {
    val isSelected = date == selected
    Column(modifier.heightIn(min = 56.dp)
        .background(if (isSelected) Lime else Color.Transparent, RoundedCornerShape(8.dp))
        .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Ink else Color.Transparent, RoundedCornerShape(8.dp))
        .selectable(isSelected, role = Role.Button, onClick = onClick)
        .semantics(mergeDescendants = true) { contentDescription = "$date, $count sessions${if (date == today) ", today" else ""}" }
        .padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${date.day}", fontWeight = if (isSelected || date == today) FontWeight.Bold else FontWeight.Normal, color = Ink)
        Box(Modifier.width(16.dp).height(2.dp).background(if (date == today) Ink else Color.Transparent))
        Text(if (count > 0) "•" else "", fontSize = 12.sp, color = Ink)
    }
}

@Composable
private fun HistoryPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
}

@Composable
private fun HistoryMetric(value: String, label: String, modifier: Modifier) {
    Column(modifier.background(Color.White, RoundedCornerShape(18.dp)).padding(16.dp)) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text(label, color = Moss)
    }
}
