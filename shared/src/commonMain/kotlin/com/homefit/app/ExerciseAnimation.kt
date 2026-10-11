package com.homefit.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homefit.core.model.Exercise
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.imageResource

@Composable
internal fun ExerciseAnimation(exercise: Exercise, playing: Boolean, modifier: Modifier = Modifier) {
    val animation = TrainerAnimation.forExercise(exercise.id)
    var phase by remember(exercise.id) { mutableFloatStateOf(0f) }
    val guidance = animation?.guidanceAt(phase)
    // Presentation state only: no MVI actions, database writes or changes to workout timing.
    LaunchedEffect(exercise.id, playing) {
        if (animation == null || !playing) return@LaunchedEffect
        var previous = withFrameNanos { it }
        while (isActive) {
            val now = withFrameNanos { it }
            val seconds = ((now - previous) / 1_000_000_000f).coerceIn(0f, .05f)
            phase = (phase + seconds / animation.cycleSeconds) % 1f
            previous = now
        }
    }
    Box(modifier.clip(RoundedCornerShape(28.dp)).background(Cream), contentAlignment = Alignment.Center) {
        if (animation == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(NavigationIcons.Workouts, contentDescription = null, modifier = Modifier.size(36.dp), tint = Moss)
                Text("Follow the written guide", color = Ink, fontSize = 14.sp)
                Text("Trainer demonstration coming soon", color = Moss, fontSize = 11.sp)
            }
        } else {
            val atlas = imageResource(animation.artwork)
            Canvas(Modifier.fillMaxSize().padding(bottom = if (guidance != null) 40.dp else 0.dp).semantics {
                contentDescription = "${exercise.name}, human trainer ${animation.viewLabel.lowercase()} demonstration${if (!playing) ", paused" else ""}${guidance?.let { ". $it" }.orEmpty()}"
            }) {
                val frame = animation.frameAt(phase)
                val side = (minOf(size.width, size.height) * .90f).toInt()
                drawImage(
                    image = atlas,
                    srcOffset = frame.offset,
                    srcSize = frame.size,
                    dstOffset = IntOffset(((size.width - side) / 2).toInt(), ((size.height - side) / 2).toInt()),
                    dstSize = IntSize(side, side),
                    filterQuality = FilterQuality.High,
                )
            }
            Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                guidance?.let { Text(it, color = Ink, fontSize = 11.sp) }
                Text("TRAINER PREVIEW · ${animation.viewLabel.uppercase()}", color = Moss, fontSize = 9.sp)
            }
        }
    }
}

@Composable
internal fun ExerciseDemo(exercise: Exercise) {
    var playing by rememberSaveable(exercise.id) { mutableStateOf(true) }
    ExerciseAnimation(exercise, playing, Modifier.fillMaxWidth().height(280.dp))
    if (TrainerAnimation.forExercise(exercise.id) != null) {
        TextButton(onClick = { playing = !playing }) {
            Text(if (playing) "Pause demonstration" else "Play demonstration")
        }
        Text(if (exercise.id == "ex_040") "Breathe comfortably at your own pace" else "Trainer preview • move at your own pace", fontSize = 12.sp, color = Moss)
    }
}
