# Trainer batch 14: Seated Knee Tuck, Cat Cow, Child's Pose

Created using the built-in image-generation tool, matching the squat trainer reference. No API fallback, new dependency, subscription or API key. Ponytail kept integration within the existing renderer, mappings and tests.

## Deliverables

| Exercise | Source beside this file | Runtime drawable | Loop |
| --- | --- | --- | --- |
| Seated Knee Tuck | `seated-knee-tuck-trainer-source.png` | `trainer_seated_knee_tuck.png` | 4 seconds |
| Cat Cow | `cat-cow-trainer-source.png` | `trainer_cat_cow.png` | 8 seconds |
| Child's Pose | `childs-pose-trainer-source.png` | `trainer_childs_pose.png` | 12 seconds |

Runtime files live in `shared/src/commonMain/composeResources/drawable/`, mechanically resized with sips to 1200 × 1200. Each sheet contains four cells in a 2 × 2 grid. Second-row crop offsets are 556, 550 and 548 respectively.

Seated Knee Tuck plays 0, 0, 1, 2, 3, 3, 2, 1. Cat Cow cycles neutral, rounded, neutral-transition, gentle extension: 0, 1, 2, 3. Child's Pose uses 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0 with a six-second visual hold. These demonstration timings do not prescribe workout cadence or duration. No timer, repetition counting, MVI or persistence changes.

## Review limitations

Small body/hand position drift and faint atlas seams remain. Cat Cow's neutral poses are intentionally similar, and the cow pose has some upward gaze. Child's Pose's final forehead contact is partly obscured by the arms. AI-generated demonstrations still need qualified movement review before release; automated tests do not validate technique. No phone installation or iOS verification was requested.

All 40 catalog exercises now have trainer artwork. This is complete preview coverage, not release approval of all poses; limitations recorded in earlier batches remain.

## Generation prompts

### seated_knee_tuck

Use case: stylized-concept. HomeFit workout sprite atlas. Input is trainer identity/style reference ONLY. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes. Polished stylized 3D anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Opaque cream #F6F5F0 background, thin moss mat, soft shadows. Identical camera, scale, proportions and mat coordinates throughout. Full body with 10% margins in every cell. No text, gridlines, arrows, ghost limbs or extra equipment. Mat baseline y82% each cell. SEATED KNEE TUCK on floor, fixed three-quarter side view facing RIGHT. Seated on sit bones, both palms flat slightly behind hips supporting balance, fingers to sides, torso slightly reclined with long neutral back. BOTH feet hover off mat throughout. Four progressive poses: 1 legs extended forward with softly bent knees and heels low hovering; 2 both knees draw in a little; 3 both knees bend further toward chest; 4 comfortable compact tuck with knees toward chest and shins nearly horizontal, torso a little more upright. Hands and seat stay at identical coordinates; no lying back, no chair, no grabbing knees, no exaggerated back rounding, no feet touching ground. Code reverses four poses to extend again.

### cat_cow

Use case: stylized-concept. HomeFit workout sprite atlas. Input is trainer identity/style reference ONLY. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes. Polished stylized 3D anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Opaque cream #F6F5F0 background, thin moss mat, soft shadows. Identical camera, scale, proportions and mat coordinates throughout. Full body with 10% margins in every cell. No text, gridlines, arrows, ghost limbs or extra equipment. Mat baseline y82% each cell. CAT COW mobility, fixed SIDE view facing RIGHT. All fours, both palms under shoulders, both knees under hips, toes relaxed, hands and knees fixed in EXACT same cell coordinates. Four poses across one gentle cycle: 1 neutral flat tabletop spine, eyes down; 2 CAT rounded spine upward with pelvis tucked and head gently nodding down; 3 neutral tabletop again with a subtle transition toward extension; 4 COW gentle shallow back extension, pelvis untucked, chest opens forward and gaze slightly ahead/down, not head thrown up. Keep shoulders over wrists, hips over knees, arms straight but soft throughout. Clearly show rounded versus gently dipped torso silhouette; no rocking hips toward heels, no extreme arch, no moving arms or legs. Code cycles these four poses.

### childs_pose

Use case: stylized-concept. HomeFit workout sprite atlas. Input is trainer identity/style reference ONLY. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes. Polished stylized 3D anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Opaque cream #F6F5F0 background, thin moss mat, soft shadows. Identical camera, scale, proportions and mat coordinates throughout. Full body with 10% margins in every cell. No text, gridlines, arrows, ghost limbs or extra equipment. Mat baseline y82% each cell. CHILD'S POSE gentle kneeling stretch, fixed three-quarter SIDE view facing RIGHT. Four setup poses: 1 tabletop on hands and knees, knees comfortably apart; 2 shift hips halfway backward toward heels as palms remain forward; 3 hips near heels, chest lowers toward thighs, arms extended forward; 4 relaxed folded kneeling position, hips resting toward heels, forehead gently on mat, both arms comfortably extended palms down. Knees and shins remain grounded, hands at SAME forward mat coordinates, no bouncing or forced stretch. Complete shoes visible behind seat, no impossible ankles. Code holds final pose then gently reverses setup. Same camera, scale and mat in all cells.
