package com.homefit.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homefit.feature.app.HistorySummary
import kotlinx.datetime.LocalDate

@Composable
internal expect fun WeeklyHistoryChart(summary: HistorySummary, selected: LocalDate, onSelect: (LocalDate) -> Unit)

@Composable
internal fun ComposeWeeklyHistoryChart(summary: HistorySummary, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
    HistoryDayButtons(summary, selected, onSelect, showBars = true)
}

@Composable
internal fun HistoryDayButtons(summary: HistorySummary, selected: LocalDate, onSelect: (LocalDate) -> Unit, showBars: Boolean = false) {
    val maximum = summary.counts.maxOrNull()?.coerceAtLeast(1) ?: 1
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        summary.dates.forEachIndexed { index, date ->
            val count = summary.counts[index]
            val isSelected = date == selected
            Column(Modifier.weight(1f).heightIn(min = 48.dp)
                .border(if (isSelected) 2.dp else 0.dp, if (isSelected) TextPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                .selectable(isSelected, role = Role.Button) { onSelect(date) }
                .semantics(mergeDescendants = true) { contentDescription = "$date, $count sessions" }
                .padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$count", fontWeight = FontWeight.Bold)
                if (showBars) Box(Modifier.height(84.dp).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                    if (count > 0) Box(Modifier.width(18.dp).height((76f * count / maximum).dp)
                        .background(Accent, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)))
                    else Box(Modifier.width(18.dp).height(2.dp).background(TextSecondary))
                }
                Text(date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp)
            }
        }
    }
}
