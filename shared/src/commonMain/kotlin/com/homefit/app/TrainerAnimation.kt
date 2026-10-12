package com.homefit.app

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import homefit.shared.generated.resources.Res
import homefit.shared.generated.resources.trainer_squat
import homefit.shared.generated.resources.trainer_march
import homefit.shared.generated.resources.trainer_arm_circles
import homefit.shared.generated.resources.trainer_wall_push_up
import homefit.shared.generated.resources.trainer_glute_bridge
import homefit.shared.generated.resources.trainer_bird_dog
import homefit.shared.generated.resources.trainer_side_step
import homefit.shared.generated.resources.trainer_hamstring_stretch
import homefit.shared.generated.resources.trainer_deep_breathing
import homefit.shared.generated.resources.trainer_hip_circles
import homefit.shared.generated.resources.trainer_chair_squat
import homefit.shared.generated.resources.trainer_calf_raise
import homefit.shared.generated.resources.trainer_side_lying_leg_raise
import homefit.shared.generated.resources.trainer_good_morning
import homefit.shared.generated.resources.trainer_hip_flexor_stretch
import homefit.shared.generated.resources.trainer_shoulder_rolls
import homefit.shared.generated.resources.trainer_dead_bug
import homefit.shared.generated.resources.trainer_prone_w_raise
import homefit.shared.generated.resources.trainer_knee_plank
import homefit.shared.generated.resources.trainer_chest_opener
import homefit.shared.generated.resources.trainer_torso_rotation
import homefit.shared.generated.resources.trainer_step_jack
import homefit.shared.generated.resources.trainer_standing_knee_drive
import homefit.shared.generated.resources.trainer_low_impact_skater
import homefit.shared.generated.resources.trainer_standing_side_crunch
import homefit.shared.generated.resources.trainer_knee_push_up
import homefit.shared.generated.resources.trainer_reverse_lunge
import homefit.shared.generated.resources.trainer_static_split_squat
import homefit.shared.generated.resources.trainer_jumping_jack
import homefit.shared.generated.resources.trainer_bridge_march
import homefit.shared.generated.resources.trainer_clamshell
import homefit.shared.generated.resources.trainer_donkey_kick
import homefit.shared.generated.resources.trainer_front_plank
import homefit.shared.generated.resources.trainer_modified_side_plank
import homefit.shared.generated.resources.trainer_superman
import homefit.shared.generated.resources.trainer_fast_feet_tap
import homefit.shared.generated.resources.trainer_heel_touch
import homefit.shared.generated.resources.trainer_seated_knee_tuck
import homefit.shared.generated.resources.trainer_cat_cow
import homefit.shared.generated.resources.trainer_childs_pose
import org.jetbrains.compose.resources.DrawableResource

/** Per-exercise artwork and pose sequences; presentation only, independent of workout timing. */
internal enum class TrainerAnimation(
    val exerciseId: String,
    val artwork: DrawableResource,
    val cycleSeconds: Float,
    val viewLabel: String,
    private val sequence: List<Int>,
    private val columns: Int,
    private val cellSize: Int,
    private val secondRowTop: Int,
    val guidance: String? = null,
) {
    // Five distinct squat poses; reverse the descent for ascent. Hold both endpoints
    // for two steps (0.72s each) so the standing and lowered positions are easy to follow.
    // Exclude the generated return frames (scale drift) and the deepest squat pose.
    Squat("ex_011", Res.drawable.trainer_squat, 3.6f, "Side view",
        listOf(0, 0, 2, 3, 4, 6, 6, 4, 3, 2), 4, 300, 289),
    March("ex_001", Res.drawable.trainer_march, 2.8f, "Front view",
        listOf(0, 1, 2, 3), 2, 600, 596),
    ArmCircles("ex_003", Res.drawable.trainer_arm_circles, 3.2f, "Three-quarter view",
        listOf(0, 1, 2, 3), 2, 600, 590),
    WallPushUp("ex_008", Res.drawable.trainer_wall_push_up, 3.6f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 576),
    GluteBridge("ex_016", Res.drawable.trainer_glute_bridge, 4f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 574),
    BirdDog("ex_021", Res.drawable.trainer_bird_dog, 4.8f, "Three-quarter view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 558,
        "Repeat on the other side"),
    SideStep("ex_006", Res.drawable.trainer_side_step, 3.2f, "Front view",
        listOf(0, 1, 2, 3), 2, 600, 600),
    HamstringStretch("ex_038", Res.drawable.trainer_hamstring_stretch, 12f, "Side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 574,
        "Hold gently • switch sides"),
    DeepBreathing("ex_040", Res.drawable.trainer_deep_breathing, 8f, "Three-quarter view",
        listOf(0, 1, 2, 3), 2, 600, 600),
    HipCircles("ex_004", Res.drawable.trainer_hip_circles, 4f, "Three-quarter view",
        listOf(0, 1, 2, 3), 2, 600, 593,
        "Small circles • then reverse"),
    ChairSquat("ex_012", Res.drawable.trainer_chair_squat, 4f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 560,
        "Use a stable chair"),
    CalfRaise("ex_015", Res.drawable.trainer_calf_raise, 3.2f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 593,
        "Lift heels • lower slowly"),
    SideLyingLegRaise("ex_018", Res.drawable.trainer_side_lying_leg_raise, 4f, "Front-side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 543,
        "Repeat on the other side"),
    GoodMorning("ex_031", Res.drawable.trainer_good_morning, 4f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 558,
        "Hinge at hips • keep back neutral"),
    HipFlexorStretch("ex_037", Res.drawable.trainer_hip_flexor_stretch, 12f, "Side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 597,
        "Hold gently • switch sides"),
    ShoulderRolls("ex_002", Res.drawable.trainer_shoulder_rolls, 4f, "Three-quarter view",
        listOf(0, 1, 2, 3), 2, 600, 593,
        "Roll shoulders • keep arms relaxed"),
    DeadBug("ex_022", Res.drawable.trainer_dead_bug, 4.8f, "Elevated front view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 591,
        "Opposite arm + leg • switch sides"),
    ProneWRaise("ex_027", Res.drawable.trainer_prone_w_raise, 4f, "Elevated three-quarter view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 574,
        "Lift bent arms gently • keep neck long"),
    // Setup, six-second visual hold, then release; these are not repetition loops.
    KneePlank("ex_023", Res.drawable.trainer_knee_plank, 12f, "Side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 566,
        "Hold steady • breathe naturally"),
    ChestOpener("ex_039", Res.drawable.trainer_chest_opener, 12f, "Three-quarter side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 599,
        "Open chest gently • keep hands low"),
    TorsoRotation("ex_005", Res.drawable.trainer_torso_rotation, 4f, "Front view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 594,
        "Turn gently • repeat other direction"),
    // Generated cell 2 has shoulder-height arms; play it before the higher arms in cell 1.
    StepJack("ex_007", Res.drawable.trainer_step_jack, 4f, "Front view",
        listOf(0, 0, 2, 1, 3, 3, 1, 2), 2, 600, 600,
        "Step, don't jump • alternate sides"),
    StandingKneeDrive("ex_028", Res.drawable.trainer_standing_knee_drive, 4f, "Three-quarter side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 592,
        "Lift knee with control • switch sides"),
    LowImpactSkater("ex_029", Res.drawable.trainer_low_impact_skater, 4f, "Front view",
        listOf(0, 1, 2, 3), 2, 600, 597,
        "Step side to side • no jumping"),
    StandingSideCrunch("ex_032", Res.drawable.trainer_standing_side_crunch, 4f, "Front view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 589,
        "Bend gently • switch sides"),
    KneePushUp("ex_009", Res.drawable.trainer_knee_push_up, 4f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 555,
        "Knees supported • lower with control"),
    ReverseLunge("ex_013", Res.drawable.trainer_reverse_lunge, 4.8f, "Three-quarter side view",
        // Keep the crop below the first row's shoes, even though baseline alignment is imperfect.
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 570,
        "Step back • switch sides"),
    StaticSplitSquat("ex_014", Res.drawable.trainer_static_split_squat, 4f, "Three-quarter side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 565,
        "Keep split stance • switch sides"),
    JumpingJack("ex_010", Res.drawable.trainer_jumping_jack, 3.2f, "Front view",
        listOf(0, 1, 2, 3), 2, 600, 600,
        "Jump out and in • land softly"),
    BridgeMarch("ex_017", Res.drawable.trainer_bridge_march, 4.8f, "Elevated three-quarter side view",
        listOf(0, 1, 2, 3), 2, 600, 550,
        "Keep hips steady • alternate legs"),
    Clamshell("ex_019", Res.drawable.trainer_clamshell, 4f, "Front-side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 576,
        "Keep heels together • switch sides"),
    DonkeyKick("ex_020", Res.drawable.trainer_donkey_kick, 4f, "Three-quarter side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 600,
        "Keep hips square • switch sides"),
    FrontPlank("ex_024", Res.drawable.trainer_front_plank, 12f, "Side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 543,
        "Hold steady • breathe naturally"),
    ModifiedSidePlank("ex_025", Res.drawable.trainer_modified_side_plank, 12f, "Front-side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 592,
        "Hold with knees supported • switch sides"),
    Superman("ex_026", Res.drawable.trainer_superman, 4f, "Side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 560,
        "Lift gently • keep neck long"),
    FastFeetTap("ex_030", Res.drawable.trainer_fast_feet_tap, 2f, "Front view",
        listOf(0, 1, 2, 3), 2, 600, 583,
        "Small alternating taps • stay light"),
    HeelTouch("ex_033", Res.drawable.trainer_heel_touch, 4f, "Elevated front view",
        listOf(0, 1, 2, 3), 2, 600, 598,
        "Reach side to side • keep feet planted"),
    SeatedKneeTuck("ex_034", Res.drawable.trainer_seated_knee_tuck, 4f, "Three-quarter side view",
        listOf(0, 0, 1, 2, 3, 3, 2, 1), 2, 600, 556,
        "Draw knees in • keep hands supported"),
    CatCow("ex_035", Res.drawable.trainer_cat_cow, 8f, "Side view",
        listOf(0, 1, 2, 3), 2, 600, 550,
        "Round and soften gently • breathe naturally"),
    ChildsPose("ex_036", Res.drawable.trainer_childs_pose, 12f, "Three-quarter side view",
        listOf(0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0), 2, 600, 548,
        "Rest gently • breathe naturally");

    // Optional visual pacing, not a breath-hold instruction or a workout-timer update.
    fun guidanceAt(phase: Float): String? = when (this) {
        DeepBreathing -> if (normalizedPhase(phase) < .5f) "Breathe in gently" else "Breathe out gently"
        else -> guidance
    }

    private fun normalizedPhase(phase: Float): Float =
        if (phase.isFinite()) ((phase % 1f) + 1f) % 1f else 0f

    fun frameAt(phase: Float): TrainerFrame {
        val normalized = normalizedPhase(phase)
        val cell = sequence[(normalized * sequence.size).toInt().coerceAtMost(sequence.lastIndex)]
        // Align the floor baseline of each generated atlas's second row.
        return TrainerFrame(
            IntOffset((cell % columns) * cellSize, if (cell < columns) 0 else secondRowTop),
            IntSize(cellSize, cellSize),
        )
    }

    companion object {
        fun forExercise(id: String): TrainerAnimation? = entries.firstOrNull { it.exerciseId == id }
    }
}

internal data class TrainerFrame(val offset: IntOffset, val size: IntSize)
