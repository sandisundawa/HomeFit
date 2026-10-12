# Trainer batch 04: Lower Body Foundation, first half

Generated with the built-in image-generation tool using the existing squat trainer as a character/style reference. The image-generation skill guided reference matching, per-asset generation, visual inspection and refinement. No stock-media purchase, API key or new subscription was added.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Visual loop |
| --- | --- | --- | --- | --- |
| Hip Circles | `hip-circles-trainer-source.png` | `trainer_hip_circles.png` | 4 | 4 seconds |
| Chair Squat | `chair-squat-trainer-source.png` | `trainer_chair_squat.png` | 4 | 4 seconds |
| Calf Raise | `calf-raise-trainer-source.png` | `trainer_calf_raise.png` | 4 | 3.2 seconds |

Sources are retained here. Runtime PNGs live in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 using `sips`. All use 2 × 2 sheets with per-asset floor-baseline crop offsets. These are original illustrated sprite animations, not live rigged 3D models.

Hip Circles progresses through four positions in one direction, with a reminder to reverse. Chair Squat and Calf Raise reverse their four pose sequences to show the return movement. Guidance and animation remain local presentation state and do not change repetition counting, workout duration or persistence.

## Review notes

A targeted Chair Squat edit attempted to improve inconsistent shoe positions. The selected refinement still has visible horizontal foot-position drift and is an illustrative preview, not release-ready movement instruction. Floor-baseline alignment does not correct that horizontal drift. Hip Circles is intentionally subtle and also needs motion review on a phone. All generated poses need qualified movement review before release.

Lower Body Foundation now has six of nine demonstrations. Side-Lying Leg Raise, Good Morning and Hip Flexor Stretch remain. Full Body Foundation remains fully covered.

## Generation prompts

### hip_circles

Use case: stylized-concept. Original HomeFit exercise sprite atlas. Input image is ONLY a trainer identity/clothing/rendering reference, NOT motion or layout. Same adult athletic male, tan skin, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural human proportions. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells. Full person in each cell, at least 10% margin for head and shoes, identical scale/camera. Four distinct ordered poses left-right top-bottom. Fully OPAQUE solid warm cream #F6F5F0 background. No labels, text, arrows, gridlines, motion trails, extra limbs, or transparency. Ground at 88% cell height throughout. Exercise STANDING HIP CIRCLES, NOT leg circles. Fixed front three-quarter camera. Both feet shoulder width, flat on floor at exactly the same positions in every frame. Knees soft. Hands rest on hips. Torso mostly upright, head and shoulders relatively quiet. Four subtle but distinct positions around one small horizontal hip circle: 1 pelvis shifted LEFT, 2 pelvis gently shifted FORWARD, 3 pelvis shifted RIGHT, 4 pelvis gently shifted BACK. Pelvis moves relative to planted feet; body never rotates to a different camera view. Neutral comfortable spine, no deep squat, no lifted knees, no backward arch, no twisting legs. Show hand/hip displacement clearly without exaggerated leaning. All frames one same person at same size.

### chair_squat

Use case: stylized-concept. Original HomeFit exercise sprite atlas. Input image is ONLY a trainer identity/clothing/rendering reference, NOT motion or layout. Same adult athletic male, tan skin, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural human proportions. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells. Full person in each cell, at least 10% margin for head and shoes, identical scale/camera. Four distinct ordered poses left-right top-bottom. Fully OPAQUE solid warm cream #F6F5F0 background. No labels, text, arrows, gridlines, motion trails, extra limbs, or transparency. Ground at 88% cell height throughout. Exercise CHAIR SQUAT, fixed strict side view facing RIGHT. Include the SAME sturdy armless four-legged wooden chair BEHIND trainer (left side), seat around knee height, fixed chair geometry/position throughout. Both shoes flat hip-width and fixed slightly in front of seat. Arms straight forward for balance, back neutral, knees follow toes. Four distinct descent poses: 1 standing tall just in front of chair; 2 small bend sending hips back toward seat; 3 halfway lower with hips still above chair; 4 hips lightly TOUCHING the seat, thighs near horizontal, torso modestly forward. No collapsing, no hovering far above seat in final frame, no chair intersecting body, no sliding feet, no disappearing chair legs. Code reverses these four frames for standing up. Full chair and trainer inside each square cell with margins.

### calf_raise

Use case: stylized-concept. Original HomeFit exercise sprite atlas. Input image is ONLY a trainer identity/clothing/rendering reference, NOT motion or layout. Same adult athletic male, tan skin, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural human proportions. NEW square sheet with EXACTLY 2 columns x 2 rows, four equally sized square cells. Full person in each cell, at least 10% margin for head and shoes, identical scale/camera. Four distinct ordered poses left-right top-bottom. Fully OPAQUE solid warm cream #F6F5F0 background. No labels, text, arrows, gridlines, motion trails, extra limbs, or transparency. Ground at 88% cell height throughout. Exercise STANDING DOUBLE-LEG CALF RAISE, strict side view facing RIGHT. Arms relaxed at sides, torso vertical and knees softly straight. Same foot forefoot/toe positions fixed on level floor. Four DISTINCT rising poses: 1 both soles flat on floor, heels down; 2 heels lift a little, ankle plantarflexion begins; 3 heels halfway raised with visible gap under heel soles; 4 heels fully comfortably raised about 6cm, weight on BALLS of both feet, toes remain in contact with floor. Entire body rises vertically as heels rise, no knee squat, no jump or floating toes, no tip-of-toe ballet pose. Sneakers must visibly tilt with heel elevated, not remain flat hovering in air. Head may rise slightly but never crop. Same ground baseline all four cells. Code reverses sequence for slow controlled lowering.

## Chair Squat refinement prompt

Refine this exact 2x2 CHAIR SQUAT sprite atlas. Preserve trainer identity, clothing, cream opaque background, camera, four poses and wooden chair. Correct ONLY inconsistent foot/ankle placement and associated leg geometry. In EACH square cell, pin the same shoes to exactly the same floor coordinates: heels at 48% cell width, toe tips at 65% cell width, floor at 88% cell height. Shoes flat in all four frames; no step or slide. Chair stays identical with seat from 20% to 46% cell width at 66% cell height, legs fixed. Frame1 standing just in front of chair, frame2 shallow squat, frame3 lower squat above seat, frame4 hips touching chair seat. Knees bend naturally while ankles stay fixed; adjust hip/knee bend to reach chair without moving feet. Full trainer and chair visible inside all cells. Keep exact 2x2 grid, no divider lines, no text.
