package com.homefit.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.homefit.core.model.Workout
import com.homefit.core.model.WorkoutSection
import org.jetbrains.compose.resources.imageResource

@Composable
internal fun ScreenTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = TextPrimary),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
internal fun Badge(text: String) {
    Surface(color = AccentSoft, contentColor = AccentText, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun SectionHeading(title: String, action: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
        TextButton(onClick, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) { Text(action) }
    }
}

@Composable
internal fun MetricStrip(metrics: List<Pair<String, String>>) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth < 320.dp || LocalDensity.current.fontScale > 1.2f) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                metrics.forEach { (value, label) ->
                    Row(Modifier.fillMaxWidth().background(SurfaceMuted, RoundedCornerShape(24.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = TextSecondary)
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                metrics.forEach { (value, label) ->
                    Column(Modifier.weight(1f).background(SurfaceMuted, RoundedCornerShape(24.dp)).padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(value, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
internal fun WorkoutCard(workout: Workout, action: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF292B2F), Color(0xFF121416))))
        .clickable(onClick = onClick)
        .padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Badge(workout.category.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() })
            Text("${workout.durationMinutes} min", color = Color.White.copy(.8f), fontSize = 13.sp)
        }
        BoxWithConstraints {
            val showArtwork = maxWidth >= 260.dp && LocalDensity.current.fontScale <= 1.2f
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(workout.name, color = Color.White, fontSize = 25.sp, lineHeight = 29.sp, fontWeight = FontWeight.SemiBold)
                    Text("${workout.exercises.size} movements\nNo equipment", color = Color(0xFFCBCBCD), fontSize = 13.sp, lineHeight = 20.sp)
                }
                // A still from the existing artwork; the exercise player keeps its original animation.
                val trainer = workout.exercises.firstOrNull { it.section == WorkoutSection.MAIN }
                    ?.exercise?.let { TrainerAnimation.forExercise(it.id) }
                if (trainer != null && showArtwork) {
                    val atlas = imageResource(trainer.artwork)
                    val frame = trainer.frameAt(0f)
                    Canvas(Modifier.weight(1f).aspectRatio(.85f).clip(RoundedCornerShape(24.dp)).background(AccentSoft)) {
                        val side = minOf(size.width, size.height).toInt()
                        drawImage(atlas, srcOffset = frame.offset, srcSize = frame.size,
                            dstOffset = IntOffset(((size.width - side) / 2).toInt(), ((size.height - side) / 2).toInt()),
                            dstSize = IntSize(side, side), filterQuality = FilterQuality.High)
                    }
                }
            }
        }
        PrimaryButton(action, onClick)
    }
}
