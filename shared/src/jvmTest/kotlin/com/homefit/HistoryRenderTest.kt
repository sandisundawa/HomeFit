package com.homefit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import com.homefit.app.*
import com.homefit.feature.app.*
import kotlinx.datetime.LocalDate
import java.awt.EventQueue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalComposeUiApi::class)
class HistoryRenderTest {
    @Test fun `history renders empty and populated calendars at phone widths`() {
        EventQueue.invokeAndWait {
            val date = LocalDate(2026, 10, 15)
            val records = listOf(12, 14, 15, 15).mapIndexed { index, day ->
                CompletedWorkout("test", "Full Body Foundation", index.toLong(), LocalDate(2026, 10, day), "09:30")
            }
            val directory = File("build/reports/history").apply { mkdirs() }
            for (width in listOf(320, 390)) {
                for ((name, history) in listOf("empty" to emptyList(), "populated" to records)) {
                    val scene = ImageComposeScene(width = width, height = 1400) {
                        HomeFitTheme {
                            Box(Modifier.fillMaxSize().background(Cream)) {
                                HistoryScreen(AppState(historyDate = date, workoutHistory = history)) { }
                            }
                        }
                    }
                    try {
                        repeat(3) { scene.render(it * 33_333_333L).close() }
                        val bytes = scene.render(100_000_000L).use { image -> image.encodeToData()!!.use { it.bytes } }
                        assertFalse(bytes.isEmpty())
                        File(directory, "$name-$width.png").writeBytes(bytes)
                    } finally { scene.close() }
                }
            }
        }
    }
}
