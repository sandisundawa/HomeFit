package com.homefit

import com.homefit.app.TrainerAnimation
import com.homefit.core.data.SeedCatalog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrainerAnimationTest {
    @Test fun `squat uses five poses with endpoint holds and a reversed ascent`() {
        val animation = TrainerAnimation.Squat
        // Sample each step at its midpoint to avoid floating-point frame boundaries.
        val steps = (0 until 10).map { animation.frameAt((it + .5f) / 10f) }
        assertEquals(5, steps.distinct().size)
        assertEquals(steps[0], steps[1])
        assertEquals(steps[5], steps[6])
        assertEquals(steps[4], steps[7])
        assertEquals(steps[3], steps[8])
        assertEquals(steps[2], steps[9])
        assertEquals(3.6f, animation.cycleSeconds)
    }

    @Test fun `trainer artwork maps to exactly the supported exercises`() {
        val expected = mapOf(
            "ex_011" to "Bodyweight Squat", "ex_001" to "March in Place",
            "ex_003" to "Arm Circles", "ex_008" to "Wall Push-Up",
            "ex_016" to "Glute Bridge", "ex_021" to "Bird Dog", "ex_006" to "Side Step",
            "ex_038" to "Hamstring Stretch", "ex_040" to "Deep Breathing",
            "ex_004" to "Hip Circles", "ex_012" to "Chair Squat", "ex_015" to "Calf Raise",
            "ex_018" to "Side-Lying Leg Raise", "ex_031" to "Good Morning", "ex_037" to "Hip Flexor Stretch",
            "ex_002" to "Shoulder Rolls", "ex_022" to "Dead Bug", "ex_027" to "Prone W Raise",
            "ex_023" to "Knee Plank", "ex_039" to "Chest Opener",
            "ex_005" to "Torso Rotation", "ex_007" to "Step Jack", "ex_028" to "Standing Knee Drive",
            "ex_029" to "Low-Impact Skater", "ex_032" to "Standing Side Crunch",
            "ex_009" to "Knee Push-Up", "ex_013" to "Reverse Lunge", "ex_014" to "Static Split Squat",
            "ex_010" to "Jumping Jack", "ex_017" to "Bridge March", "ex_019" to "Clamshell",
            "ex_020" to "Donkey Kick", "ex_024" to "Front Plank", "ex_025" to "Modified Side Plank",
            "ex_026" to "Superman", "ex_030" to "Fast Feet Tap", "ex_033" to "Heel Touch",
            "ex_034" to "Seated Knee Tuck", "ex_035" to "Cat Cow", "ex_036" to "Child's Pose",
        )
        assertEquals(expected.keys, TrainerAnimation.entries.map { it.exerciseId }.toSet())
        expected.forEach { (id, name) ->
            assertEquals(name, SeedCatalog.exercises.single { it.id == id }.name)
            assertEquals(id, TrainerAnimation.forExercise(id)?.exerciseId)
        }
        assertEquals(SeedCatalog.exercises.map { it.id }.toSet(), expected.keys)
        assertEquals(expected.size, TrainerAnimation.entries.size)
        assertNull(TrainerAnimation.forExercise("unknown"))
    }

    @Test fun `new exercises each use four distinct poses within the atlas`() {
        TrainerAnimation.entries.filter { it != TrainerAnimation.Squat }.forEach { animation ->
            val frames = (0 until 400).map { animation.frameAt((it + .5f) / 400f) }
            assertEquals(4, frames.distinct().size, animation.name)
            frames.forEach { frame ->
                assertTrue(frame.offset.x >= 0 && frame.offset.x + frame.size.width <= 1200)
                assertTrue(frame.offset.y >= 0 && frame.offset.y + frame.size.height <= 1200)
            }
        }
    }

    @Test fun `cyclic demos advance through all four poses in order`() {
        listOf(TrainerAnimation.March, TrainerAnimation.ArmCircles, TrainerAnimation.SideStep,
            TrainerAnimation.HipCircles, TrainerAnimation.ShoulderRolls, TrainerAnimation.LowImpactSkater,
            TrainerAnimation.JumpingJack, TrainerAnimation.BridgeMarch,
            TrainerAnimation.FastFeetTap, TrainerAnimation.HeelTouch, TrainerAnimation.CatCow).forEach { animation ->
            val frames = (0 until 4).map { animation.frameAt((it + .5f) / 4f) }
            assertEquals(listOf(0, 600, 0, 600), frames.map { it.offset.x })
            assertEquals(0, frames[0].offset.y)
            assertEquals(0, frames[1].offset.y)
            assertTrue(frames[2].offset.y > 0)
            assertEquals(frames[2].offset.y, frames[3].offset.y)
        }
    }

    @Test fun `out and back demos hold endpoints and reverse the movement`() {
        listOf(TrainerAnimation.WallPushUp, TrainerAnimation.GluteBridge, TrainerAnimation.BirdDog,
            TrainerAnimation.ChairSquat, TrainerAnimation.CalfRaise,
            TrainerAnimation.SideLyingLegRaise, TrainerAnimation.GoodMorning,
            TrainerAnimation.DeadBug, TrainerAnimation.ProneWRaise,
            TrainerAnimation.TorsoRotation, TrainerAnimation.StepJack, TrainerAnimation.StandingKneeDrive,
            TrainerAnimation.StandingSideCrunch, TrainerAnimation.KneePushUp,
            TrainerAnimation.ReverseLunge, TrainerAnimation.StaticSplitSquat,
            TrainerAnimation.Clamshell, TrainerAnimation.DonkeyKick, TrainerAnimation.Superman,
            TrainerAnimation.SeatedKneeTuck).forEach { animation ->
            val frames = (0 until 8).map { animation.frameAt((it + .5f) / 8f) }
            assertEquals(frames[0], frames[1])
            assertEquals(frames[4], frames[5])
            assertEquals(frames[3], frames[6])
            assertEquals(frames[2], frames[7])
        }
    }

    @Test fun `single side strength demos include a side switch reminder`() {
        assertEquals("Repeat on the other side", TrainerAnimation.BirdDog.guidance)
        assertEquals("Repeat on the other side", TrainerAnimation.SideLyingLegRaise.guidance)
        assertEquals("Opposite arm + leg • switch sides", TrainerAnimation.DeadBug.guidance)
        assertEquals("Lift knee with control • switch sides", TrainerAnimation.StandingKneeDrive.guidance)
        assertEquals("Bend gently • switch sides", TrainerAnimation.StandingSideCrunch.guidance)
        assertEquals("Step back • switch sides", TrainerAnimation.ReverseLunge.guidance)
        assertEquals("Keep split stance • switch sides", TrainerAnimation.StaticSplitSquat.guidance)
        assertEquals("Keep heels together • switch sides", TrainerAnimation.Clamshell.guidance)
        assertEquals("Keep hips square • switch sides", TrainerAnimation.DonkeyKick.guidance)
        assertEquals("Lift gently • keep neck long", TrainerAnimation.Superman.guidance)
        assertEquals("Small alternating taps • stay light", TrainerAnimation.FastFeetTap.guidance)
        assertEquals("Reach side to side • keep feet planted", TrainerAnimation.HeelTouch.guidance)
        assertEquals("Draw knees in • keep hands supported", TrainerAnimation.SeatedKneeTuck.guidance)
        assertEquals("Round and soften gently • breathe naturally", TrainerAnimation.CatCow.guidance)
        assertEquals("Keep hips steady • alternate legs", TrainerAnimation.BridgeMarch.guidance)
        assertEquals("Jump out and in • land softly", TrainerAnimation.JumpingJack.guidance)
        assertEquals("Step side to side • no jumping", TrainerAnimation.LowImpactSkater.guidance)
    }

    @Test fun `step jack reorders generated cells to raise arms progressively`() {
        val animation = TrainerAnimation.StepJack
        val frames = (0 until 8).map { animation.frameAt((it + .5f) / 8f) }
        assertEquals(listOf(0, 0, 0, 600, 600, 600, 600, 0), frames.map { it.offset.x })
        assertEquals(listOf(0, 0, 600, 0, 600, 600, 0, 600), frames.map { it.offset.y })
        assertEquals("Step, don't jump • alternate sides", animation.guidance)
        assertEquals("Turn gently • repeat other direction", TrainerAnimation.TorsoRotation.guidance)
    }

    @Test fun `every Low Impact Conditioning exercise has a trainer demonstration`() {
        val workouts = SeedCatalog.workouts.filter { it.name == "Low Impact Conditioning" }
        assertEquals(4, workouts.size)
        workouts.forEach { workout ->
            assertEquals(9, workout.exercises.size)
            workout.exercises.forEach { item ->
                assertEquals(item.exercise.id, TrainerAnimation.forExercise(item.exercise.id)?.exerciseId)
            }
        }
    }

    @Test fun `every Upper Body and Core exercise has a trainer demonstration`() {
        val workouts = SeedCatalog.workouts.filter { it.name == "Upper Body + Core" }
        assertEquals(4, workouts.size)
        workouts.forEach { workout ->
            assertEquals(9, workout.exercises.size)
            workout.exercises.forEach { item ->
                assertEquals(item.exercise.id, TrainerAnimation.forExercise(item.exercise.id)?.exerciseId)
            }
        }
    }

    @Test fun `every Lower Body Foundation exercise has a trainer demonstration`() {
        val workouts = SeedCatalog.workouts.filter { it.name == "Lower Body Foundation" }
        assertEquals(4, workouts.size)
        workouts.forEach { workout ->
            assertEquals(9, workout.exercises.size)
            workout.exercises.forEach { item ->
                assertEquals(item.exercise.id, TrainerAnimation.forExercise(item.exercise.id)?.exerciseId)
            }
        }
    }

    @Test fun `every Full Body Foundation exercise has a trainer demonstration`() {
        val workouts = SeedCatalog.workouts.filter { it.name == "Full Body Foundation" }
        assertEquals(4, workouts.size)
        workouts.forEach { workout ->
            assertEquals(9, workout.exercises.size)
            workout.exercises.forEach { item ->
                assertEquals(item.exercise.id, TrainerAnimation.forExercise(item.exercise.id)?.exerciseId)
            }
        }
    }

    @Test fun `stretch previews hold the stretch rather than bouncing`() {
        listOf(TrainerAnimation.HamstringStretch, TrainerAnimation.HipFlexorStretch).forEach { animation ->
            val frames = (0 until 12).map { animation.frameAt((it + .5f) / 12f) }
            assertEquals(4, frames.distinct().size)
            (3..8).forEach { assertEquals(frames[3], frames[it]) }
            assertEquals(frames[2], frames[9])
            assertEquals(frames[1], frames[10])
            assertEquals(frames[0], frames[11])
            assertEquals(12f, animation.cycleSeconds)
            assertEquals("Hold gently • switch sides", animation.guidanceAt(.5f))
        }
    }

    @Test fun `breathing cues match the two inhale and two exhale frames`() {
        val animation = TrainerAnimation.DeepBreathing
        assertEquals(8f, animation.cycleSeconds)
        assertEquals("Breathe in gently", animation.guidanceAt(0f))
        assertEquals("Breathe in gently", animation.guidanceAt(.25f))
        assertEquals("Breathe out gently", animation.guidanceAt(.5f))
        assertEquals("Breathe out gently", animation.guidanceAt(.75f))
        assertEquals("Breathe in gently", animation.guidanceAt(1f))
    }

    @Test fun `hold demos keep final setup before releasing`() {
        val cues = mapOf(
            TrainerAnimation.KneePlank to "Hold steady • breathe naturally",
            TrainerAnimation.FrontPlank to "Hold steady • breathe naturally",
            TrainerAnimation.ModifiedSidePlank to "Hold with knees supported • switch sides",
            TrainerAnimation.ChestOpener to "Open chest gently • keep hands low",
            TrainerAnimation.ChildsPose to "Rest gently • breathe naturally",
        )
        cues.forEach { (animation, cue) ->
            val frames = (0 until 12).map { animation.frameAt((it + .5f) / 12f) }
            assertEquals(4, frames.distinct().size)
            (3..8).forEach { assertEquals(frames[3], frames[it]) }
            assertEquals(frames[2], frames[9])
            assertEquals(frames[1], frames[10])
            assertEquals(frames[0], frames[11])
            assertEquals(12f, animation.cycleSeconds)
            assertEquals(cue, animation.guidanceAt(.5f))
        }
    }

    @Test fun `selected frames fit atlas and never use drifting return rows`() {
        repeat(361) {
            val frame = TrainerAnimation.Squat.frameAt(it / 360f)
            assertTrue(frame.offset.x >= 0 && frame.offset.x + frame.size.width <= 1200)
            assertTrue(frame.offset.y >= 0 && frame.offset.y + frame.size.height <= 600)
        }
    }

    @Test fun `loop wraps to standing and includes a lowered pose`() {
        TrainerAnimation.entries.forEach { animation ->
            assertEquals(animation.frameAt(0f), animation.frameAt(1f))
            assertEquals(animation.frameAt(0f), animation.frameAt(2f))
            assertNotEquals(animation.frameAt(0f), animation.frameAt(.5f))
            assertEquals(animation.frameAt(0f), animation.frameAt(Float.NaN))
        }
    }
}
