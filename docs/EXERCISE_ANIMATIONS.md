# Human-trainer demonstration preview

The original stick-figure Canvas renderer and its procedural pose generator have been removed at the user's request.

## Current coverage

- Bodyweight Squat (ex_011): original stylized human trainer, moss gym shirt, charcoal shorts, light trainers; side-view frame animation.
- March in Place (ex_001): four alternating-leg poses, front view, 2.8-second loop.
- Arm Circles (ex_003): four circular phases, three-quarter view, 3.2-second loop.
- Wall Push-Up (ex_008): four descent poses reversed for ascent, side view, 3.6-second loop. Straight and bent positions each hold for two of eight steps.
- Glute Bridge (ex_016): four lifting poses reversed for lowering, side view, 4-second loop.
- Bird Dog (ex_021): four single-side poses from tabletop to extension and back, three-quarter view, 4.8-second loop. A visible and accessible reminder says to repeat on the other side.
- Side Step (ex_006): four lateral step/tap poses, front view, 3.2-second loop.
- Hamstring Stretch (ex_038): four standing setup poses, side view, 12-second visual loop with a six-second held pose and switch-side reminder.
- Deep Breathing (ex_040): four subtle breathing frames, three-quarter view, eight-second visual loop with gentle inhale/exhale captions and no breath-hold phase.
- Hip Circles (ex_004): four subtle hip positions, three-quarter view, four-second loop with a direction-reversal reminder.
- Chair Squat (ex_012): four descent poses reversed for standing, side view, four-second loop with a stable-chair reminder. The generated sheet still has some horizontal foot drift and needs refinement before release.
- Calf Raise (ex_015): four heel-raise poses reversed for lowering, side view, 3.2-second loop.
- Side-Lying Leg Raise (ex_018): four lifting poses reversed for lowering, front-side view, four-second loop with a side-switch reminder.
- Good Morning (ex_031): four bodyweight hip-hinge poses reversed for standing, side view, four-second loop.
- Hip Flexor Stretch (ex_037): four half-kneeling setup poses, side view, twelve-second visual loop with a six-second held pose and switch-side reminder.
- Shoulder Rolls (ex_002): four shoulder-circle positions, three-quarter view, four-second loop with a relaxed-arm reminder.
- Dead Bug (ex_022): four single-side opposite-arm/leg positions reversed for return, elevated front view, 4.8-second loop with a side-switch reminder.
- Prone W Raise (ex_027): four small bent-arm lifts reversed for lowering, elevated three-quarter view, four-second loop.
- Knee Plank (ex_023): four forearm-supported setup poses, side view, twelve-second visual loop with a six-second held pose and natural-breathing reminder.
- Chest Opener (ex_039): four standing setup poses with hands joined low behind the hips, three-quarter side view, twelve-second visual loop with a six-second held pose.
- Torso Rotation (ex_005): four small upper-body rotation poses reversed for return, front view, four-second loop with an other-direction reminder.
- Step Jack (ex_007): four grounded step-and-arm-raise poses reversed for return, front view, four-second loop with an alternate-sides reminder. Middle atlas cells are reordered for rising arms.
- Standing Knee Drive (ex_028): four rear-tap-to-knee-lift poses reversed for return, three-quarter side view, four-second loop with a side-switch reminder.
- Low-Impact Skater (ex_029): four alternating grounded step/tap poses, front view, four-second loop with a no-jumping reminder.
- Standing Side Crunch (ex_032): four same-side knee/elbow approach poses reversed for return, front view, four-second loop with a side-switch reminder.
- Knee Push-Up (ex_009): four knee-supported descent poses reversed for ascent, side view, four-second loop.
- Reverse Lunge (ex_013): four standing-to-backward-lunge poses reversed for return, three-quarter side view, 4.8-second loop with a side-switch reminder. Front-foot drift remains a known preview limitation.
- Static Split Squat (ex_014): four split-stance descent poses reversed for ascent, three-quarter side view, four-second loop with a side-switch reminder.
- Jumping Jack (ex_010): four opening/closing jump poses, front view, 3.2-second loop with a soft-landing reminder.
- Bridge March (ex_017): four alternating bridge-march poses, elevated three-quarter side view, 4.8-second loop with a steady-hips reminder.
- Clamshell (ex_019): four bent-knee opening poses reversed for closing, front-side view, four-second loop with a heels-together and side-switch reminder.
- Donkey Kick (ex_020): four bent-knee lifting poses reversed for return, three-quarter side view, four-second loop with a side-switch reminder.
- Front Plank (ex_024): four forearm-plank setup poses, side view, twelve-second visual loop with a six-second hold and natural-breathing reminder.
- Modified Side Plank (ex_025): four knee-supported side-plank setup poses, front-side view, twelve-second visual loop with a six-second hold and side-switch reminder.
- Superman (ex_026): four prone lifting poses reversed for lowering, side view, four-second loop.
- Fast Feet Tap (ex_030): four alternating small foot-contact poses, front view, two-second demonstration loop.
- Heel Touch (ex_033): four alternating heel-reaching poses, elevated front view, four-second loop.
- Seated Knee Tuck (ex_034): four seated tuck poses reversed for extension, three-quarter side view, four-second loop.
- Cat Cow (ex_035): four neutral/rounded/neutral/extended poses, side view, eight-second loop.
- Child's Pose (ex_036): four kneeling setup poses with a six-second visual hold, three-quarter side view, twelve-second loop.
- All 40 catalog exercises now have trainer artwork, including all nine demonstrations in each of the four workout routines across all four week variants. Unknown exercise IDs retain the written-guide fallback.

Preview coverage is complete, but the library still needs visual refinements and qualified movement review before release. These are raster frame animations with a 3D-rendered appearance, not rigged 3D models or generated video. The approved squat remains unchanged: five distinct poses play forward and backward across ten steps in a 3.6-second loop. Standing and lowered positions each hold for 0.72 seconds; intermediate poses last 0.36 seconds. Expect visible frame stepping; no optical-flow interpolation is used.

## Assets

The artwork was generated and refined using the built-in image-generation tool. No stock assets, paid libraries, new subscriptions or API keys were used. Original artwork and prompts are in `assets/trainers/`. The app bundles a 1200px atlas at `shared/src/commonMain/composeResources/drawable/trainer_squat.png` (approximately 1.3 MB).

For the squat, five descent frames (zero-based atlas cells 0, 2, 3, 4, 6) are used. The generated return frames drift in scale, so the same descent poses are reversed for the ascent. Source crop offsets align the second-row floor baseline. Do not assume the unused squat atlas cells are production-ready.

The three new exercises use separate 1200px, 2 × 2 atlases. Their artwork sources, final prompts and generation notes are listed in `assets/trainers/BATCH_01.md`.

The next three (Glute Bridge, Bird Dog and Side Step) use the same atlas format; see `assets/trainers/BATCH_02.md`. The accepted Bird Dog sheet deliberately demonstrates only one side; it is not an alternating-side animation.

The two cooldown demonstrations use the same 2 × 2 format; see `assets/trainers/BATCH_03.md` for sources and prompts. The stretch demo shows one side. Its held pose is a visual example, not a prescribed stretch duration. Breathing changes are intentionally subtle; the captions provide optional pacing, and the detail screen reminds users to breathe comfortably at their own pace.

The first Lower Body Foundation batch uses the same format; see `assets/trainers/BATCH_04.md` for sources, generation/refinement prompts and known visual limitations.

The final Lower Body Foundation batch uses three more 2 × 2 sheets; see `assets/trainers/BATCH_05.md` for sources, prompts and review notes. The leg raise and hip-flexor stretch illustrate one side, with reminders to switch sides. No workout timer or repetition-counting changes accompany these visual loops.

The first Upper Body + Core batch adds Shoulder Rolls, Dead Bug and Prone W Raise; see `assets/trainers/BATCH_06.md` for final prompts, source paths and rejected Dead Bug draft notes. The selected Dead Bug uses an elevated front camera to make the opposite-limb pairing clearer. Shoulder Rolls and Prone W Raise have deliberately small ranges of motion.

The final Upper Body + Core batch adds Knee Plank and Chest Opener; see `assets/trainers/BATCH_07.md` for sources and prompts. Both show setup, a held pose, then release. The repeating visual example does not prescribe hold duration or replace the workout timer.

The first Low Impact Conditioning batch adds Torso Rotation, Step Jack and Standing Knee Drive; see `assets/trainers/BATCH_08.md` for sources, prompts, pose reordering and known supporting-foot drift. These show one direction/side at a time, with reminders to switch.

## Playback

The final catalog batch adds Seated Knee Tuck, Cat Cow and Child's Pose; see `assets/trainers/BATCH_14.md` for assets, full prompts and limitations. Child's Pose shows setup, hold and release rather than repeated bouncing.

The fourth wider-catalog batch adds Superman, Fast Feet Tap and Heel Touch; see `assets/trainers/BATCH_13.md` for assets, full prompts, the foot-alternation correction and review limitations.

The third wider-catalog batch adds Donkey Kick, Front Plank and Modified Side Plank; see `assets/trainers/BATCH_12.md` for assets, prompts and visual limitations. Plank loops demonstrate setup and a hold, not repeated push-ups.

The second wider-catalog batch adds Jumping Jack, Bridge March and Clamshell; see `assets/trainers/BATCH_11.md` for source/runtime paths, full prompts and review limits. Bridge March alternates legs; Clamshell demonstrates one side.

The first wider-catalog batch adds Knee Push-Up, Reverse Lunge and Static Split Squat; see `assets/trainers/BATCH_10.md` for saved assets, prompts and known foot/hand drift. Reverse Lunge returns to standing; Static Split Squat stays in a split stance.

The final Low Impact Conditioning batch adds Low-Impact Skater and Standing Side Crunch; see `assets/trainers/BATCH_09.md` for sources, prompts and visual limitations. Skater shows both directions; Side Crunch shows one side with a switch reminder.

- `TrainerAnimation.kt` maps catalog IDs to artwork, crop coordinates and playback sequence.
- `ExerciseAnimation.kt` renders the bundled artwork with the Compose frame clock.
- Exercise details have a Play/Pause demonstration control.
- Get Ready shows the first pose; Exercise plays the loop; Rest uses the existing rest card.
- Workout Pause removes the player composable, stopping playback. Returning starts a fresh demonstration loop without resetting the workout timer.
- Animation is local presentation state, not MVI events or Room writes. It never counts repetitions.

## Verification

Run `./gradlew :shared:jvmTest :appAndroid:assembleDebug` with the Android SDK configured.

Tests cover exact full-catalog mapping, complete coverage of all four workout routines, pose counts, atlas crop boundaries, loop wrapping, sequence order (including Step Jack reordering), reminders, plank/stretch holds and breathing-caption alignment. Rendering tests check visible playback, frozen paused frames and resumed playback independently for all 40 exercises. JVM rendering previews are generated per exercise ID at `shared/build/reports/exercise-animation/`.

Rendered standing/lowered poses have been visually inspected. No physical-device or iOS test has been performed for this replacement. AI-generated exercise poses still need qualified movement review before release; rendering tests do not certify exercise technique.
