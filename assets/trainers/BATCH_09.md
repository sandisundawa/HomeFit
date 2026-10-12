# Trainer batch 09: Low Impact Conditioning completion

Created with the built-in image-generation tool. The image-generation skill guided character-reference matching and separate four-pose sheets. Skater generation required one retry after a connection error. No API fallback, API key, stock-media purchase or new subscription was used.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Loop |
| --- | --- | --- | --- | --- |
| Low-Impact Skater | `low-impact-skater-trainer-source.png` | `trainer_low_impact_skater.png` | 4 | 4 seconds |
| Standing Side Crunch | `standing-side-crunch-trainer-source.png` | `trainer_standing_side_crunch.png` | 4 | 4 seconds |

Master PNGs are beside this document. Runtime PNGs are in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 with `sips`. Both use 2 × 2 sheets with second-row floor-baseline alignment.

Skater cycles 0, 1, 2, 3 to show both directions with grounded steps and rear toe taps. The two centered transitions have similar arm positions; they are not a perfectly mirrored motion cycle. Side Crunch uses 0, 0, 1, 2, 3, 3, 2, 1 with held endpoints and a reminder to switch sides. The supporting foot/body has slight generated position drift. These are stepped illustrated previews, not rigged models.

Playback remains presentation-only; workout timing, repetition targets, MVI and persistence are unchanged. Generated technique still needs qualified movement review before release. Tests verify artwork mapping, crop bounds, sequence and pause/resume, not biomechanical accuracy.

All four workout routines now have all nine demonstrations in every week variant. Twenty-five of forty catalog exercises have artwork; fifteen retain written guidance. No device installation or iOS test was performed for this batch.

## Final generation prompts

### low_impact_skater

Use case: stylized-concept. HomeFit trainer animation sprite atlas. Reference is ONLY trainer identity/style, NOT squat poses or layout. Same adult male with tan skin, dark short hair, moss fitted tee, charcoal shorts, white ankle socks, cream/lime shoes, premium stylized 3D illustration. Exactly 2 columns x 2 rows, four equal square cells, one full figure each, read left-right top-bottom. Identical fixed front camera, body proportions and scale across frames, full fingers/head/shoes with 8% margins. Opaque flat warm cream #F6F5F0 background, subtle floor shadow, floor baseline 88% in each cell. No text, arrows, gridlines, extra limbs or ghosting. LOW-IMPACT SKATER: four phases of grounded lateral step and gentle rear toe tap, NOT jumping or deep curtsy lunge. 1 weight on softly bent RIGHT leg at image left, LEFT toes lightly touch floor diagonally behind right foot, left arm naturally forward and right arm back; 2 uncross into a centered small wide stance with both feet on floor, weight transferring toward LEFT, arms passing near torso; 3 weight on softly bent LEFT leg at image right, RIGHT toes lightly touch floor diagonally behind left foot, right arm forward and left arm back; 4 centered small wide stance again, weight transferring toward RIGHT, arms passing in the opposite swing. Both centered poses visibly differ in weight and arm swing. At least one whole supporting sole grounded throughout; rear toe tap also on floor at endpoints. Hips travel side-to-side but body height mostly steady, slight comfortable hip hinge, head neutral, no deep knee bend or twisting of supporting knee. Keep feet and limbs distinct, no airborne pose. Code loops these four poses in order to alternate sides.

### standing_side_crunch

Use case: stylized-concept. HomeFit trainer animation sprite atlas. Reference is ONLY trainer identity/style, NOT squat poses or layout. Same adult male with tan skin, dark short hair, moss fitted tee, charcoal shorts, white ankle socks, cream/lime shoes, premium stylized 3D illustration. Exactly 2 columns x 2 rows, four equal square cells, one full figure each, read left-right top-bottom. Identical fixed front camera, body proportions and scale across frames, full fingers/head/shoes with 8% margins. Opaque flat warm cream #F6F5F0 background, subtle floor shadow, floor baseline 88% in each cell. No text, arrows, gridlines, extra limbs or ghosting. STANDING SIDE CRUNCH: demonstrate one side in four progressive poses, fixed front camera. LEFT supporting foot (image RIGHT) stays flat and pinned to identical spot throughout, support knee soft. LEFT hand rests on left hip. RIGHT fingertips lightly beside right temple, right elbow out toward image LEFT, never pulling neck. 1 tall stance with right foot resting just outside hip width; 2 RIGHT knee begins lifting outward to image LEFT, right elbow begins lowering toward it; 3 right knee higher and gentle right side bend, right elbow closer; 4 comfortable same-side elbow-to-knee approach near waist, small lateral crunch, no forced contact, knee points outward rather than forward. Head stays aligned with torso, chest faces camera, no twisting, no forward sit-up or deep sideways collapse, no hop. Exactly two arms and legs, supporting foot stays fixed; same knee and elbow move in all frames. Code reverses four poses to return, caption says switch sides.
