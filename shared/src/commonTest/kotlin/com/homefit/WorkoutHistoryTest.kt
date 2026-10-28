package com.homefit

import com.homefit.core.data.SeedCatalog
import com.homefit.feature.app.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*
import kotlin.time.Instant

class WorkoutHistoryTest {
    private val instant = Instant.parse("2026-10-15T18:30:00Z")
    private val workout = SeedCatalog.workouts.first()
    private fun finished() = AppState(route = AppRoute.PLAYER,
        player = PlayerState(workout, phase = PlayerPhase.FINISHED))

    @Test fun `completion records local date time and name`() {
        val result = finished().recordCompletion(instant, TimeZone.of("Asia/Jakarta"))
        val record = result.workoutHistory.single()
        assertEquals(LocalDate(2026, 10, 16), record.localDate)
        assertEquals("01:30", record.localTime)
        assertEquals(workout.name, record.workoutName)
        assertEquals(instant.toEpochMilliseconds(), record.completedAtEpochMs)
        assertEquals(AppRoute.COMPLETE, result.route)
        assertTrue(result.player!!.completionRecorded)
    }

    @Test fun `duplicate finish is ignored even after returning to player`() {
        val result = finished().recordCompletion(instant, TimeZone.UTC)
        assertEquals(result, result.recordCompletion(instant, TimeZone.UTC))
        assertEquals(1, result.copy(route = AppRoute.PLAYER).recordCompletion(instant, TimeZone.UTC).workoutHistory.size)
        val restored = Json.decodeFromString<AppState>(Json.encodeToString(result))
        assertEquals(1, restored.copy(route = AppRoute.PLAYER).recordCompletion(instant, TimeZone.UTC).workoutHistory.size)
    }

    @Test fun `unfinished or abandoned player is not recorded`() {
        val active = finished().copy(player = PlayerState(workout))
        assertEquals(active, active.recordCompletion(instant, TimeZone.UTC))
        assertEquals(AppState(), AppState().recordCompletion(instant, TimeZone.UTC))
    }

    @Test fun `repeating workout counts sessions separately but day once`() {
        var state = finished()
        repeat(3) {
            state = state.copy(route = AppRoute.PLAYER, player = PlayerState(workout, phase = PlayerPhase.FINISHED))
                .recordCompletion(instant, TimeZone.UTC)
        }
        val summary = historySummary(state.workoutHistory, LocalDate(2026, 10, 15))
        assertEquals(3, summary.sessions)
        assertEquals(1, summary.activeDays)
        assertEquals(listOf(0, 0, 0, 3, 0, 0, 0), summary.counts)
        assertEquals(1, state.completedWorkoutIds.size)
        assertTrue(state.earlierWorkoutIds.isEmpty())
    }

    @Test fun `old snapshots preserve undated workouts without inventing sessions`() {
        val old = Json.decodeFromString<AppState>("""{"completedWorkoutIds":["old"]}""")
        assertEquals(setOf("old"), old.earlierWorkoutIds)
        assertTrue(old.workoutHistory.isEmpty())
        val updated = old.copy(route = AppRoute.PLAYER, player = PlayerState(workout, phase = PlayerPhase.FINISHED))
            .recordCompletion(instant, TimeZone.UTC)
        assertEquals(setOf("old"), updated.earlierWorkoutIds)
        assertEquals(updated, Json.decodeFromString<AppState>(Json.encodeToString(updated)))
    }

    @Test fun `serialization preserves completion date independently of current timezone`() {
        val state = finished().recordCompletion(instant, TimeZone.of("Asia/Jakarta"))
            .copy(historyDate = LocalDate(2026, 10, 16))
        val restored = Json.decodeFromString<AppState>(Json.encodeToString(state))
        assertEquals(state, restored)
        assertEquals(1, historySummary(restored.workoutHistory, LocalDate(2026, 10, 16)).sessions)
        assertEquals(LocalDate(2026, 10, 16), restored.workoutHistory.single().localDate)
    }

    @Test fun `weeks begin monday and span month and year boundaries`() {
        assertEquals(LocalDate(2025, 12, 29), historyWeek(LocalDate(2026, 1, 1)).first())
        assertEquals(LocalDate(2026, 1, 4), historyWeek(LocalDate(2026, 1, 1)).last())
        assertEquals(LocalDate(2026, 9, 28), historyWeek(LocalDate(2026, 10, 1)).first())
    }

    @Test fun `month shifting clamps dates including leap day`() {
        assertEquals(LocalDate(2024, 2, 29), shiftHistoryMonth(LocalDate(2024, 1, 31), 1))
        assertEquals(LocalDate(2025, 2, 28), shiftHistoryMonth(LocalDate(2025, 3, 31), -1))
        assertEquals(LocalDate(2026, 1, 31), shiftHistoryMonth(LocalDate(2025, 12, 31), 1))
    }

    @Test fun `calendar includes all days and pads complete weeks`() {
        val leap = historyMonth(LocalDate(2024, 2, 12))
        assertEquals(29, leap.filterNotNull().size)
        assertEquals(0, leap.size % 7)
        assertEquals(3, leap.takeWhile { it == null }.size)
        assertEquals(28, historyMonth(LocalDate(2025, 2, 12)).filterNotNull().size)
    }

    @Test fun `empty history has zero counts`() {
        val summary = historySummary(emptyList(), LocalDate(2026, 10, 15))
        assertEquals(0, summary.sessions)
        assertEquals(0, summary.activeDays)
        assertEquals(List(7) { 0 }, summary.counts)
    }

    @Test fun `changing chart week never reuses previous week counts`() {
        val records = finished().recordCompletion(instant, TimeZone.UTC).workoutHistory
        val populated = historySummary(records, LocalDate(2026, 10, 15))
        val empty = historySummary(records, LocalDate(2026, 10, 22))
        assertEquals(1, populated.counts[3])
        assertEquals(List(7) { 0 }, empty.counts)
        assertEquals(LocalDate(2026, 10, 22), empty.dates[3])
        assertEquals(populated, historySummary(records, LocalDate(2026, 10, 15)))
    }

    @Test fun `date intents keep week and month derived from the same selection`() {
        val model = HomeFitViewModel()
        model.dispatch(AppIntent.SelectHistoryDate(LocalDate(2024, 1, 31)))
        model.dispatch(AppIntent.ChangeHistoryMonth(1))
        val selected = model.state.value.historyDate!!
        assertEquals(LocalDate(2024, 2, 29), selected)
        assertTrue(selected in historyWeek(selected))
        assertTrue(selected in historyMonth(selected))
        model.dispatch(AppIntent.HistoryToday)
        assertEquals(today(), model.state.value.historyDate)
        model.dispatch(AppIntent.SelectHistoryDate(LocalDate(2020, 1, 1)))
        model.dispatch(AppIntent.Navigate(AppRoute.HISTORY))
        assertEquals(today(), model.state.value.historyDate)
    }
}
