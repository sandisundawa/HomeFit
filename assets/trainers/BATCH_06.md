# Trainer batch 06: Upper Body + Core

Generated with the built-in image-generation tool. The image-generation skill guided character-reference matching, per-asset generation, inspection and a new Dead Bug camera view after rejecting its first drafts. No stock-media purchase, API key or new subscription was added.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Visual loop |
| --- | --- | --- | --- | --- |
| Shoulder Rolls | `shoulder-rolls-trainer-source.png` | `trainer_shoulder_rolls.png` | 4 | 4 seconds |
| Dead Bug | `dead-bug-trainer-source.png` | `trainer_dead_bug.png` | 4 | 4.8 seconds |
| Prone W Raise | `prone-w-raise-trainer-source.png` | `trainer_prone_w_raise.png` | 4 | 4 seconds |

Master PNGs are saved beside this document. Runtime assets are in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 with `sips`. All are 2 × 2 sprite sheets with second-row baseline alignment.

Shoulder Rolls cycles forward/up/back/down. Dead Bug and Prone W Raise reverse their four poses for the return, with held endpoints. Dead Bug demonstrates one opposite arm-and-leg pair and includes a side-switch reminder. Playback remains local presentation state: no changes to MVI, persistence, workout timing or repetition counting.

## Review notes

The initial side-view Dead Bug sheet appeared to use same-side limbs. A targeted edit did not resolve that clearly and also produced unwanted background artifacts; neither draft is bundled. The selected replacement uses an elevated front view to separate the moving opposite limbs. The extended leg reaches its long position earlier than the arm, so this remains a stepped demonstration rather than synchronized rigged motion.

Shoulder Rolls and Prone W Raise intentionally use small movements. Generated sheets can contain minor body/anchor drift; crop alignment is not anatomical correction. All illustrations remain trainer previews requiring qualified movement review before release. Automated rendering tests check playback and pause/resume, not exercise technique.

Upper Body + Core now has seven of nine demonstrations in each week variant; Knee Plank and Chest Opener remain. Eighteen of forty catalog exercises have artwork. No device installation or iOS test was performed for this batch.

## Final prompts

### shoulder_rolls

Use case: stylized-concept.
Asset type: HomeFit human-trainer animation sprite atlas.
Input image: character and rendering STYLE REFERENCE ONLY, not its squat movement or grid.
Same adult male trainer: tan skin, short dark hair, fitted moss-green T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration with natural anatomy.
Square sheet with EXACTLY 2 columns × 2 rows, four equally sized square cells; one complete trainer per cell, reading left-right then top-bottom. Identical camera, body proportions, scale and clothing across cells. Whole person including fingers and shoes visible with at least 8% margin. Opaque flat warm cream #F6F5F0 background and soft ground shadow. No text, arrows, gridlines, watermark, motion trails or extra limbs.
Movement: STANDING SHOULDER ROLLS, front three-quarter view facing slightly RIGHT. Both feet flat hip-width and fixed, head and torso upright and stationary, arms hanging loosely at sides with elbows almost straight. Four distinct stages of a small backward shoulder circle: 1 both shoulders rounded slightly FORWARD; 2 shoulders raised UP toward ears; 3 shoulders drawn gently BACK with collarbones broad; 4 shoulders relaxed DOWN. Arms move only passively with shoulder position, never lifted sideways or overhead. Not arm circles, not a neck roll, no body twist or leaning. Shoulder silhouette must visibly change at small display size while head and feet remain fixed. All soles at y=88% within each cell. Code cycles these four poses in order; no return frames needed.

### dead_bug

Use case: stylized-concept.
Create a NEW HomeFit DEAD BUG four-pose sprite atlas, exactly 2 columns and 2 rows of equal square cells on OPAQUE flat warm cream #F6F5F0. The reference supplies ONLY trainer identity/style: same tan adult male, dark hair, moss tee, charcoal shorts, white socks, cream/lime sneakers, stylized 3D render. Do not use squat poses.
Camera is fixed HIGH OBLIQUE OVERHEAD from above trainer's feet, looking down along body at 65 degrees; body head toward TOP of each cell, feet toward BOTTOM. This viewpoint must show all four limbs separated and clear. Trainer lies face UP on moss mat, head/back/pelvis supported. Full person and mat inside each cell with 8% margins and same scale/anchor.
Critical movement: opposite arm and leg. On IMAGE LEFT, the ARM moves upward overhead alongside head; on IMAGE RIGHT, the LEG extends downward away from hip. The image-RIGHT ARM stays raised vertically above shoulder, and image-LEFT LEG stays in tabletop with bent knee over hip. Do not move arm and leg on same image side.
Four progressive poses left-right top-bottom: (1) both arms pointing toward camera/ceiling above shoulders, both hips and knees bent 90 degrees tabletop; (2) image-left arm reaches a quarter toward overhead top of cell, image-right leg begins extending downward, opposite limbs stationary; (3) the same moving pair halfway extended; (4) image-left arm fully overhead near floor and image-right leg comfortably almost straight hovering above mat, with stationary image-right arm still up and image-left knee still bent over hip. Exactly two arms and two legs in every pose, natural anatomy, no duplicate limbs, no arms merging. Neutral neck, relaxed head, no crunch, no arch. No labels, arrows, grid lines, ghosting, cutout transparency or black backgrounds. Four visibly distinct poses, code reverses for return.

### prone_w_raise

Use case: stylized-concept.
Asset type: HomeFit human-trainer animation sprite atlas.
Input image: character and rendering STYLE REFERENCE ONLY, not its squat movement or grid.
Same adult male trainer: tan skin, short dark hair, fitted moss-green T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration with natural anatomy.
Square sheet with EXACTLY 2 columns × 2 rows, four equally sized square cells; one complete trainer per cell, reading left-right then top-bottom. Identical camera, body proportions, scale and clothing across cells. Whole person including fingers and shoes visible with at least 8% margin. Opaque flat warm cream #F6F5F0 background and soft ground shadow. No text, arrows, gridlines, watermark, motion trails or extra limbs.
Movement: PRONE W RAISE, lying FACE DOWN on same thin moss exercise mat, head toward LEFT, straight legs toward RIGHT. Camera elevated at 45 degrees from the head/front-side so BOTH bent arms and the W shape across upper back are clearly visible, with entire person in frame. Elbows bent about 90 degrees and out from torso, hands near head, forming W; never straighten into Y or Superman. Forehead faces mat, neck stays long, chest/pelvis/legs stay down. Four distinct progressive lift poses: 1 bent W-shaped arms resting on mat; 2 both hands and elbows lift very slightly; 3 lift halfway; 4 both bent arms hover a few centimeters above mat with shoulder blades gently drawn together/down. Whole bent arm lifts, not push-up pressing through palms. Keep identical elbow angles and head/body position; do not lift legs, arch back or turn face toward camera. Make hand/elbow-to-mat clearance visible from this camera angle. Body/mat fixed at same position and scale across cells, mat baseline y=80%. Code reverses four poses to lower again.

## Rejected Dead Bug attempts (provenance)

### Initial side view

Use case: stylized-concept.
Asset type: HomeFit human-trainer animation sprite atlas.
Input image: character and rendering STYLE REFERENCE ONLY, not its squat movement or grid.
Same adult male trainer: tan skin, short dark hair, fitted moss-green T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration with natural anatomy.
Square sheet with EXACTLY 2 columns × 2 rows, four equally sized square cells; one complete trainer per cell, reading left-right then top-bottom. Identical camera, body proportions, scale and clothing across cells. Whole person including fingers and shoes visible with at least 8% margin. Opaque flat warm cream #F6F5F0 background and soft ground shadow. No text, arrows, gridlines, watermark, motion trails or extra limbs.
Movement: DEAD BUG, lying on BACK on the same thin moss mat. Head LEFT and feet RIGHT. Elevated three-quarter side camera sees both arms and legs clearly. Head, pelvis and back stay supported on mat, neck neutral, no crunch or back arch. Demonstrate ONE opposite arm/leg pair moving: trainer's RIGHT arm extends overhead toward left edge, while trainer's LEFT leg extends toward right edge; LEFT arm stays vertical above shoulder and RIGHT hip/knee remain bent 90 degrees. Four distinct stages: 1 both arms vertical and both knees in tabletop 90 degrees; 2 right arm moves a little toward overhead while opposite left leg begins extending; 3 those same limbs extend halfway; 4 right arm comfortably overhead and left leg almost straight hovering above mat, the stationary left arm vertical and right leg still tabletop. Exactly two arms and two legs, visually separated rather than merged. Keep the same limb identity across all frames. Full overhead hand and extended shoe must fit every square; use consistent scale sized for longest pose. Mat and body anchor at same position each cell, mat baseline y=80%. Code reverses four poses for return; caption will tell user to repeat other side.

### Targeted edit

Edit this exact 2x2 DEAD BUG exercise sprite sheet. Preserve the trainer identity, clothing, four cells, cream background, mat, camera and supported head/back. Correct the SAME-SIDE limb pairing into an OPPOSITE ARM AND LEG pairing. In this viewpoint, the NEAR arm (closest to camera, emerging from foreground shoulder) moves overhead to LEFT, but the FAR leg (behind the foreground leg) must extend to RIGHT. The NEAR leg closest to viewer MUST remain bent at hip and knee 90 degrees in EVERY frame, held upright in front of the extending far leg. The FAR arm remains vertical in every frame. Important: the shorts and thigh closest to camera belong to the FIXED bent leg; the straight moving leg emerges BEHIND that bent leg. Show enough spacing/perspective to distinguish both knees.
Four ordered poses: top-left neutral both arms vertical and both legs tabletop; top-right near arm tilted slightly toward left and FAR leg partly extending, with bent moving knee still visible; bottom-left near arm halfway overhead and FAR leg almost extended; bottom-right near arm overhead and FAR leg extended hovering above mat, while NEAR leg still tabletop. Exactly two arms and two legs, no limb duplication or ghosting. Same fixed limb identity in all frames. Keep pelvis/back/head on mat without crunching. Reduce ALL four figures equally by 10% if necessary so even extended hand and shoe have at least 6% clear margin inside their own cell; no crossing cell boundaries. Keep identical mat/body scale and anchor in each cell. No text or arrows.
