# Trainer batch 07: Upper Body + Core completion

Generated with the built-in image-generation tool. The image-generation skill guided matching the existing trainer, separate sprite-sheet generation and visual inspection. An initial connection failure required one retry; no API fallback, API key, stock-media purchase or new subscription was used.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Visual loop |
| --- | --- | --- | --- | --- |
| Knee Plank | `knee-plank-trainer-source.png` | `trainer_knee_plank.png` | 4 | 12 seconds |
| Chest Opener | `chest-opener-trainer-source.png` | `trainer_chest_opener.png` | 4 | 12 seconds |

Master PNGs are saved beside this document. Runtime PNGs live in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 with `sips`. Each sheet is 2 × 2, with second-row crop offsets aligning the mat/floor baseline.

Both previews progress through four setup poses, hold the last pose for six seconds, and reverse the setup to release. Knee Plank is a supported forearm hold, not a push-up. Chest Opener keeps joined hands low behind the pelvis. The visual cycle is illustrative: it is not a prescribed hold duration or an instruction to release every twelve seconds. The workout timer remains authoritative. No MVI, persistence or repetition-counting changes were made.

## Review notes

The setup-to-hold changes are intentionally subtle, especially the final two Knee Plank frames. Generated anatomy and contact positions can drift slightly between frames; baseline alignment does not eliminate those differences. These remain trainer previews needing qualified movement review before release. Automated tests validate rendering, frame sequences and pause/resume, not exercise technique.

Full Body Foundation, Lower Body Foundation and Upper Body + Core now each have all nine demonstrations across all four week variants. Twenty of forty catalog exercises have artwork. This batch was not installed on a physical device or tested on iOS.

## Final generation prompts

### knee_plank

Use case: stylized-concept.
Asset type: HomeFit exercise demonstration sprite atlas.
Input image: trainer IDENTITY/CLOTHING/RENDERING reference only. Do not copy squat or its grid.
Same tan adult male, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime trainers; premium stylized 3D illustration with natural anatomy.
Exactly 2 columns × 2 rows of four equal square cells in a square image. Full figure per cell, at least 8% margins, same proportions, scale, camera and anchors. Opaque flat warm cream #F6F5F0 backdrop, gentle shadows. No text, labels, arrows, ghost limbs, borders or watermarks.
Exercise: FOREARM KNEE PLANK, strict side view facing RIGHT, both knees supported on a thin moss mat and both forearms on mat. Knees and elbows are pinned to identical ground points in ALL frames. Elbows beneath shoulders in final pose, forearms point right; legs extend left of knees with shoes resting on mat. Camera slightly elevated enough to distinguish both forearms. FOUR progressive SETUP poses, NOT a push-up: 1 forearm kneeling with hips modestly raised above final alignment and slightly back; 2 hips shift a little forward/lower; 3 almost aligned; 4 final supported knee plank with shoulders, hips and knees making one straight diagonal line, abdomen braced and back neutral. Knees remain on mat, no full toe plank, no bent elbows lifting off mat, no sagging waist, no extreme pike, no crunch. Head stays aligned with spine, gaze at mat. Code will HOLD final pose before gently returning to setup; do not depict repetitions of push-ups. Mat floor baseline y=80% in each cell. Full hands, head, knees and shoes visible with stable scale.

### chest_opener

Use case: stylized-concept.
Asset type: HomeFit exercise demonstration sprite atlas.
Input image: trainer IDENTITY/CLOTHING/RENDERING reference only. Do not copy squat or its grid.
Same tan adult male, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime trainers; premium stylized 3D illustration with natural anatomy.
Exactly 2 columns × 2 rows of four equal square cells in a square image. Full figure per cell, at least 8% margins, same proportions, scale, camera and anchors. Opaque flat warm cream #F6F5F0 backdrop, gentle shadows. No text, labels, arrows, ghost limbs, borders or watermarks.
Exercise: STANDING CHEST OPENER STRETCH, side three-quarter view facing RIGHT, camera chosen so joined hands BEHIND pelvis are visible to LEFT of torso. Feet hip width flat and fixed, knees soft, torso tall, neutral ribs/pelvis, chin level, no backward bend. FOUR progressive SETUP poses: 1 hands loosely joined behind hips with elbows bent; 2 elbows begin to straighten while shoulders gently settle back/down; 3 arms nearly straight with hands just behind pelvis; 4 arms comfortably long, joined hands only slightly away from hips, chest gently broad, shoulders relaxed. Keep joined hands LOW behind pelvis, not lifted toward shoulder height; no aggressive shoulder extension, no deep back arch, no leaning or neck craning. Both arms/hands anatomically coherent, exactly two arms, hands clearly together. Head, body and planted shoes fixed in all frames, ground y=88% each cell. Code will hold final stretch then gradually return, not bounce. Whole figure including fingertips/shoes inside each cell.
