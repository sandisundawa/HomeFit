package com.homefit.feature.app

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class CompletedWorkout(
    val workoutId: String,
    val workoutName: String,
    val completedAtEpochMs: Long,
    val localDate: LocalDate,
    val localTime: String,
)

fun today(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun shiftHistoryMonth(date: LocalDate, offset: Int): LocalDate = date.plus(offset, DateTimeUnit.MONTH)

fun historyWeek(date: LocalDate): List<LocalDate> {
    val monday = date.minus(date.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
    return List(7) { monday.plus(it, DateTimeUnit.DAY) }
}

fun historyMonth(date: LocalDate): List<LocalDate?> {
    val first = LocalDate(date.year, date.month, 1)
    val end = first.plus(1, DateTimeUnit.MONTH)
    val dates = List(first.daysUntil(end)) { first.plus(it, DateTimeUnit.DAY) }
    val cells = List<LocalDate?>(first.dayOfWeek.isoDayNumber - 1) { null } + dates
    return cells + List((7 - cells.size % 7) % 7) { null }
}

fun AppState.recordCompletion(instant: Instant, zone: TimeZone): AppState {
    val active = player ?: return this
    if (route != AppRoute.PLAYER || active.phase != PlayerPhase.FINISHED || active.completionRecorded) return this
    val local = instant.toLocalDateTime(zone)
    val record = CompletedWorkout(active.workout.id, active.workout.name, instant.toEpochMilliseconds(),
        local.date, "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}")
    return copy(route = AppRoute.COMPLETE, player = active.copy(completionRecorded = true),
        completedWorkoutIds = completedWorkoutIds + active.workout.id, workoutHistory = workoutHistory + record)
}

data class HistorySummary(val dates: List<LocalDate>, val counts: List<Int>) {
    val activeDays: Int get() = counts.count { it > 0 }
    val sessions: Int get() = counts.sum()
}

fun historySummary(records: List<CompletedWorkout>, selected: LocalDate): HistorySummary {
    val counts = records.groupingBy { it.localDate }.eachCount()
    val week = historyWeek(selected)
    return HistorySummary(week, week.map { counts[it] ?: 0 })
}
