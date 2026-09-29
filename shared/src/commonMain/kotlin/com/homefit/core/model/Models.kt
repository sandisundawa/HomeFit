package com.homefit.core.model

import kotlinx.serialization.Serializable

@Serializable enum class FitnessGoal { WEIGHT_LOSS, GENERAL_FITNESS, BUILD_STRENGTH }
@Serializable enum class FitnessLevel { BEGINNER, INTERMEDIATE }
@Serializable enum class ExerciseCategory { WARM_UP, FULL_BODY, LOWER_BODY, UPPER_BODY, CORE, CARDIO, MOBILITY }
@Serializable enum class ExerciseType { REPS, DURATION }
@Serializable enum class ImpactLevel { LOW, MODERATE }
@Serializable enum class WorkoutSection { WARM_UP, MAIN, COOLDOWN }
@Serializable enum class ProgramDayStatus { UPCOMING, AVAILABLE, IN_PROGRESS, COMPLETED, MISSED, SKIPPED }

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val type: ExerciseType = ExerciseType.REPS,
    val defaultReps: Int? = null,
    val defaultDurationSeconds: Int? = null,
    val impact: ImpactLevel = ImpactLevel.LOW,
    val requiresFloor: Boolean = false,
    val requiresWall: Boolean = false,
    val requiresJumping: Boolean = false,
    val instructions: List<String> = emptyList(),
    val cue: String = "Move slowly and stay in control.",
)

@Serializable
data class WorkoutExercise(
    val exercise: Exercise,
    val section: WorkoutSection,
    val reps: Int? = exercise.defaultReps,
    val durationSeconds: Int? = exercise.defaultDurationSeconds,
    val restAfterSeconds: Int = 20,
)

@Serializable
data class Workout(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val week: Int,
    val slot: Int,
    val durationMinutes: Int,
    val exercises: List<WorkoutExercise>,
)

@Serializable
data class ProgramDay(
    val dayNumber: Int,
    val weekday: Int,
    val workout: Workout?,
    val status: ProgramDayStatus,
)

@Serializable
data class UserProfile(
    val goal: FitnessGoal = FitnessGoal.WEIGHT_LOSS,
    val level: FitnessLevel = FitnessLevel.BEGINNER,
    val workoutDays: Set<Int> = setOf(1, 3, 5, 7),
    val lowImpact: Boolean = true,
    val reminderEnabled: Boolean = true,
)
