# Trainer batch 05: Lower Body Foundation completion

Generated with the built-in image-generation tool using the existing squat trainer as a character/style reference. The image-generation skill guided matching the reference, separate asset generation and visual inspection. No stock-media purchase, API key or new subscription was added.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Visual loop |
| --- | --- | --- | --- | --- |
| Side-Lying Leg Raise | `side-lying-leg-raise-trainer-source.png` | `trainer_side_lying_leg_raise.png` | 4 | 4 seconds |
| Good Morning | `good-morning-trainer-source.png` | `trainer_good_morning.png` | 4 | 4 seconds |
| Hip Flexor Stretch | `hip-flexor-stretch-trainer-source.png` | `trainer_hip_flexor_stretch.png` | 4 | 12 seconds |

Sources are retained here. Runtime PNGs live in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 with `sips`. Each atlas is 2 × 2; second-row crop offsets align the floor/mat baseline.

Side-Lying Leg Raise and Good Morning reverse their four poses to return, with held endpoints. Hip Flexor Stretch holds its final pose for six seconds within its twelve-second visual loop. This is an illustrative hold, not a prescribed stretch duration. Both single-side demonstrations include a side-switch reminder. Playback is presentation-only: workout timing, repetition targets, MVI and persistence are unchanged.

## Review notes

The four poses, full-body framing and app-rendered crops were visually reviewed. AI-generated artwork may have small shifts in proportions and contact positions between poses; baseline alignment does not correct those differences. These remain trainer previews requiring qualified movement review before release, not professionally certified technique demonstrations.

Lower Body Foundation and Full Body Foundation now each have all nine demonstrations across their four week variants. Fifteen of the forty catalog exercises have artwork. No device installation was performed for this batch.

## Generation prompts

### side_lying_leg_raise

Use case: stylized-concept. HomeFit original exercise animation sprite atlas. The attached image is a CHARACTER/STYLE REFERENCE ONLY; do not reproduce its squat or layout. Same adult male trainer, tan skin, short dark hair, fitted moss T-shirt, charcoal shorts, white ankle socks and cream/lime sneakers. Premium stylized 3D illustration with natural anatomy. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells, one complete person per cell. Four distinct poses read left-right top-bottom. Entire body including fingers and shoes fits each cell with at least 10% margin. Identical camera, proportions, clothing and scale in all frames. OPAQUE flat warm cream #F6F5F0 background, no labels, text, arrows, ghost limbs, transparency or gridlines. Exercise SIDE-LYING LEG RAISE. Trainer lies on his LEFT SIDE on the SAME thin moss mat, head at image LEFT and feet at RIGHT, chest facing camera. Camera front-side slightly elevated so BOTH legs are visible. Left arm folded under head for support, right palm on mat in front of chest. Both legs long with hips stacked vertically, torso and lower left leg stationary. Four ordered poses: 1 top right leg rests on lower left leg; 2 straight top leg lifted 10 degrees sideways away from floor; 3 top leg lifted 20 degrees; 4 top leg lifted 30 degrees. Lift from hip, toes point forward toward camera, no upward external rotation, no bent-knee clamshell or kicking forward. Exactly two legs, exactly two arms. Lower leg/head/mat stay fixed; only upper leg changes. Mat surface baseline y=76% in each cell. Code reverses the poses to lower the leg; only one side is demonstrated.

### good_morning

Use case: stylized-concept. HomeFit original exercise animation sprite atlas. The attached image is a CHARACTER/STYLE REFERENCE ONLY; do not reproduce its squat or layout. Same adult male trainer, tan skin, short dark hair, fitted moss T-shirt, charcoal shorts, white ankle socks and cream/lime sneakers. Premium stylized 3D illustration with natural anatomy. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells, one complete person per cell. Four distinct poses read left-right top-bottom. Entire body including fingers and shoes fits each cell with at least 10% margin. Identical camera, proportions, clothing and scale in all frames. OPAQUE flat warm cream #F6F5F0 background, no labels, text, arrows, ghost limbs, transparency or gridlines. Exercise BODYWEIGHT GOOD MORNING, strict SIDE VIEW facing RIGHT. Feet hip-width, both soles flat and pinned to identical positions, ground y=88% each cell. Arms crossed softly over chest throughout; no weights or barbell. Four distinct descent poses: 1 standing upright with softly bent knees; 2 hips pushed backward and long straight torso hinged forward about 15 degrees; 3 hinge about 30 degrees; 4 comfortable hinge about 45 degrees from upright. Spine and neck stay neutral as one line, gaze follows torso downward; knees retain same small bend (not a squat), hips travel backward, shins almost vertical. No back rounding, no hyperextension, no toe lift, no changing foot positions. Code reverses frames for standing upright again.

### hip_flexor_stretch

Use case: stylized-concept. HomeFit original exercise animation sprite atlas. The attached image is a CHARACTER/STYLE REFERENCE ONLY; do not reproduce its squat or layout. Same adult male trainer, tan skin, short dark hair, fitted moss T-shirt, charcoal shorts, white ankle socks and cream/lime sneakers. Premium stylized 3D illustration with natural anatomy. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells, one complete person per cell. Four distinct poses read left-right top-bottom. Entire body including fingers and shoes fits each cell with at least 10% margin. Identical camera, proportions, clothing and scale in all frames. OPAQUE flat warm cream #F6F5F0 background, no labels, text, arrows, ghost limbs, transparency or gridlines. Exercise HALF-KNEELING HIP FLEXOR STRETCH, side view facing RIGHT. SAME moss mat in each frame. Right front foot flat ahead with knee near 90 degrees; left rear knee supported on mat under/just behind hip, rear shin on mat with shoe behind. Hands lightly on front thigh above knee, torso upright, shoulders relaxed, pelvis gently tucked, no low-back arch. Four distinct gradual setup poses: 1 tall neutral half-kneel; 2 gently tuck pelvis without leaning backward; 3 very small forward hip shift while torso remains tall; 4 comfortable small final shift, front shin nearly vertical, no deep lunge or backbend. Front foot and rear knee contact points PINNED in every frame; body size and mat identical. Mat baseline y=86% cell height. Only demonstrate one side. Code will hold final pose, then return gradually, not bounce. Both knees, feet and entire head in frame.
