package com.homefit.app

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.homefit.feature.app.HistorySummary
import kotlinx.datetime.LocalDate

@Composable
internal actual fun WeeklyHistoryChart(summary: HistorySummary, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
    val selectDate by rememberUpdatedState(onSelect)
    AndroidView(modifier = Modifier.fillMaxWidth().height(210.dp), factory = { context ->
        BarChart(context).apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            isScaleEnabled = false
            isDragEnabled = false
            isDoubleTapToZoomEnabled = false
            isHighlightPerDragEnabled = false
            // Compose buttons below expose all seven values and actions, including zero-height bars.
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                axisMinimum = -.5f
                axisMaximum = 6.5f
                granularity = 1f
                isGranularityEnabled = true
                isDrawGridLinesEnabled = false
                textColor = TextSecondary.toArgb()
                valueFormatter = IAxisValueFormatter { value, _ ->
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").getOrNull(value.toInt()) ?: ""
                }
            }
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                isGranularityEnabled = true
                textColor = TextSecondary.toArgb()
                gridColor = Outline.toArgb()
                isDrawAxisLineEnabled = false
                valueFormatter = IAxisValueFormatter { value, _ -> value.toInt().toString() }
            }
            onValueSelected { entry, _ -> (entry.data as? LocalDate)?.let(selectDate) }
        }
    }, update = { chart ->
        val entries = summary.dates.mapIndexed { index, date ->
            BarEntry(index.toFloat(), summary.counts[index].toFloat(), data = date)
        }
        val set = BarDataSet(entries, "Completed sessions").apply {
            colors = summary.dates.map { if (it == selected) TextPrimary.toArgb() else Accent.toArgb() }
            valueTextColor = TextPrimary.toArgb()
            valueTextSize = 12f
            valueFormatter = IValueFormatter { value, _, _, _ -> value.toInt().toString() }
            barCornerRadius = 5f
        }
        chart.axisLeft.axisMaximum = (summary.counts.maxOrNull() ?: 0).coerceAtLeast(1).toFloat() + 1f
        chart.data = BarData(set).apply { barWidth = .55f }
        chart.highlightValue(summary.dates.indexOf(selected).toFloat(), 0, callListener = false)
        chart.invalidate()
    })
    HistoryDayButtons(summary, selected, onSelect)
}
