package com.homefit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homefit.app.*
import com.homefit.core.data.SeedCatalog
import java.awt.EventQueue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse

/** Renders each actual composable independently, exercising frame playback and pause/resume. */
@OptIn(ExperimentalComposeUiApi::class)
class ExerciseAnimationRenderTest {
    @Test fun `all trainer demos animate freeze and resume`() {
        EventQueue.invokeAndWait {
            TrainerAnimation.entries.forEach { animation ->
                val playing = mutableStateOf(true)
                val exercise = SeedCatalog.exercises.single { it.id == animation.exerciseId }
                val scene = ImageComposeScene(width = 360, height = 360) {
                    HomeFitTheme {
                        Column(Modifier.fillMaxSize().background(Cream).padding(12.dp)) {
                            Text(exercise.name)
                            Spacer(Modifier.height(8.dp))
                            ExerciseAnimation(exercise, playing.value, Modifier.fillMaxWidth().height(300.dp))
                        }
                    }
                }
                try {
                    fun frame(time: Long): ByteArray = scene.render(time).use { image ->
                        image.encodeToData()!!.use { it.bytes }
                    }
                    var time = 0L
                    fun advance(count: Int): ByteArray {
                        // Drive every animation tick, but encode PNG only for sampled frames.
                        repeat(count) { time += 33_333_333L; scene.render(time).close() }
                        return frame(time)
                    }
                    val directory = File("build/reports/exercise-animation/${animation.exerciseId}").apply { mkdirs() }
                    val start = advance(3)
                    File(directory, "start.png").writeBytes(start)
                    // Sample a whole cycle for visual inspection, not only a single changed frame.
                    var moving = start
                    repeat(4) { step ->
                        moving = advance((animation.cycleSeconds * 30 / 4).toInt())
                        File(directory, "phase-$step.png").writeBytes(moving)
                        if (step == 1) assertFalse(start.contentEquals(moving), "${animation.name} must visibly move")
                    }
                    playing.value = false
                    val paused = advance(3)
                    assertContentEquals(paused, advance(30), "${animation.name} must freeze when paused")
                    playing.value = true
                    // Allow the deliberately held endpoint to finish before expecting a new pose.
                    val resumeFrames = (animation.cycleSeconds * 30 * .4f).toInt()
                    assertFalse(paused.contentEquals(advance(resumeFrames)), "${animation.name} must resume")
                } finally {
                    scene.close()
                }
            }
        }
    }
}
