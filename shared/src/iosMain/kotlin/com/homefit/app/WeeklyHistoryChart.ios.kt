package com.homefit.app

import androidx.compose.runtime.Composable
import com.homefit.feature.app.HistorySummary
import kotlinx.datetime.LocalDate

@Composable
internal actual fun WeeklyHistoryChart(summary: HistorySummary, selected: LocalDate, onSelect: (LocalDate) -> Unit) =
    ComposeWeeklyHistoryChart(summary, selected, onSelect)
