package com.homefit.core.data

import com.homefit.core.model.*

object SeedCatalog {
    private fun reps(id: String, name: String, category: ExerciseCategory, reps: Int, floor: Boolean = false, wall: Boolean = false, cue: String = "Move with control.") =
        Exercise(id, name, category, ExerciseType.REPS, defaultReps = reps, requiresFloor = floor, requiresWall = wall, instructions = listOf("Set a comfortable starting position.", "Complete each repetition with steady form.", "Stop if you feel sharp pain."), cue = cue)

    private fun timed(id: String, name: String, category: ExerciseCategory, seconds: Int, floor: Boolean = false, impact: ImpactLevel = ImpactLevel.LOW, jumping: Boolean = false) =
        Exercise(id, name, category, ExerciseType.DURATION, defaultDurationSeconds = seconds, impact = impact, requiresFloor = floor, requiresJumping = jumping, instructions = listOf("Set your posture before starting.", "Breathe naturally throughout the movement.", "Use a smaller range when needed."))

    val exercises = listOf(
        timed("ex_001", "March in Place", ExerciseCategory.WARM_UP, 40),
        timed("ex_002", "Shoulder Rolls", ExerciseCategory.MOBILITY, 30),
        timed("ex_003", "Arm Circles", ExerciseCategory.WARM_UP, 30),
        timed("ex_004", "Hip Circles", ExerciseCategory.MOBILITY, 30),
        timed("ex_005", "Torso Rotation", ExerciseCategory.MOBILITY, 30),
        timed("ex_006", "Side Step", ExerciseCategory.CARDIO, 40),
        timed("ex_007", "Step Jack", ExerciseCategory.CARDIO, 35),
        reps("ex_008", "Wall Push-Up", ExerciseCategory.UPPER_BODY, 8, wall = true, cue = "Keep your body in one straight line."),
        reps("ex_009", "Knee Push-Up", ExerciseCategory.UPPER_BODY, 6, floor = true),
        timed("ex_010", "Jumping Jack", ExerciseCategory.CARDIO, 25, impact = ImpactLevel.MODERATE, jumping = true),
        reps("ex_011", "Bodyweight Squat", ExerciseCategory.LOWER_BODY, 10, cue = "Push the floor away as you stand."),
        reps("ex_012", "Chair Squat", ExerciseCategory.LOWER_BODY, 8),
        reps("ex_013", "Reverse Lunge", ExerciseCategory.LOWER_BODY, 6),
        reps("ex_014", "Static Split Squat", ExerciseCategory.LOWER_BODY, 6),
        reps("ex_015", "Calf Raise", ExerciseCategory.LOWER_BODY, 12),
        reps("ex_016", "Glute Bridge", ExerciseCategory.LOWER_BODY, 10, floor = true, cue = "Squeeze your glutes, not your lower back."),
        reps("ex_017", "Bridge March", ExerciseCategory.CORE, 8, floor = true),
        reps("ex_018", "Side-Lying Leg Raise", ExerciseCategory.LOWER_BODY, 8, floor = true),
        reps("ex_019", "Clamshell", ExerciseCategory.LOWER_BODY, 10, floor = true),
        reps("ex_020", "Donkey Kick", ExerciseCategory.LOWER_BODY, 8, floor = true),
        reps("ex_021", "Bird Dog", ExerciseCategory.CORE, 8, floor = true, cue = "Keep your hips level."),
        reps("ex_022", "Dead Bug", ExerciseCategory.CORE, 8, floor = true),
        timed("ex_023", "Knee Plank", ExerciseCategory.CORE, 20, floor = true),
        timed("ex_024", "Front Plank", ExerciseCategory.CORE, 20, floor = true),
        timed("ex_025", "Modified Side Plank", ExerciseCategory.CORE, 20, floor = true),
        reps("ex_026", "Superman", ExerciseCategory.FULL_BODY, 8, floor = true),
        reps("ex_027", "Prone W Raise", ExerciseCategory.UPPER_BODY, 8, floor = true),
        reps("ex_028", "Standing Knee Drive", ExerciseCategory.CARDIO, 10),
        timed("ex_029", "Low-Impact Skater", ExerciseCategory.CARDIO, 30),
        timed("ex_030", "Fast Feet Tap", ExerciseCategory.CARDIO, 25),
        reps("ex_031", "Good Morning", ExerciseCategory.LOWER_BODY, 10),
        reps("ex_032", "Standing Side Crunch", ExerciseCategory.CORE, 10),
        reps("ex_033", "Heel Touch", ExerciseCategory.CORE, 12, floor = true),
        reps("ex_034", "Seated Knee Tuck", ExerciseCategory.CORE, 8, floor = true),
        timed("ex_035", "Cat Cow", ExerciseCategory.MOBILITY, 35, floor = true),
        timed("ex_036", "Child's Pose", ExerciseCategory.MOBILITY, 35, floor = true),
        timed("ex_037", "Hip Flexor Stretch", ExerciseCategory.MOBILITY, 30, floor = true),
        timed("ex_038", "Hamstring Stretch", ExerciseCategory.MOBILITY, 30),
        timed("ex_039", "Chest Opener", ExerciseCategory.MOBILITY, 30),
        timed("ex_040", "Deep Breathing", ExerciseCategory.MOBILITY, 40),
    )

    private fun ex(number: Int) = exercises[number - 1]
    private fun item(number: Int, section: WorkoutSection, reps: Int? = null, seconds: Int? = null, rest: Int = 20) =
        WorkoutExercise(ex(number), section, reps ?: ex(number).defaultReps, seconds ?: ex(number).defaultDurationSeconds, rest)

    private val templates = listOf(
        Triple("Full Body Foundation", ExerciseCategory.FULL_BODY, listOf(1, 3, 11, 8, 16, 21, 6, 38, 40)),
        Triple("Lower Body Foundation", ExerciseCategory.LOWER_BODY, listOf(1, 4, 12, 16, 15, 18, 31, 37, 40)),
        Triple("Upper Body + Core", ExerciseCategory.UPPER_BODY, listOf(1, 2, 8, 21, 22, 27, 23, 39, 40)),
        Triple("Low Impact Conditioning", ExerciseCategory.CARDIO, listOf(1, 5, 7, 28, 6, 29, 32, 38, 40)),
    )

    val workouts: List<Workout> = (1..4).flatMap { week ->
        templates.mapIndexed { index, (name, category, ids) ->
            val items = ids.mapIndexed { position, number ->
                val section = when (position) { 0, 1 -> WorkoutSection.WARM_UP; 7, 8 -> WorkoutSection.COOLDOWN; else -> WorkoutSection.MAIN }
                val base = ex(number)
                val extra = (week - 1) * 2
                item(number, section, reps = base.defaultReps?.plus(extra), seconds = base.defaultDurationSeconds?.plus((week - 1) * 5), rest = if (section == WorkoutSection.MAIN) 25 else 5)
            }
            Workout("w${week}_${index + 1}", name, category, week, index + 1, 11 + week, items)
        }
    }

    fun createProgram(days: Set<Int>): List<ProgramDay> {
        var workoutIndex = 0
        return (1..28).map { day ->
            val weekday = ((day - 1) % 7) + 1
            val workout = if (weekday in days) workouts.getOrNull(workoutIndex++) else null
            ProgramDay(day, weekday, workout, if (day == 1) ProgramDayStatus.AVAILABLE else ProgramDayStatus.UPCOMING)
        }
    }
}
