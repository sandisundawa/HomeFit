# Trainer batch 08: Low Impact Conditioning

Generated with the built-in image-generation tool. The image-generation skill guided matching the existing trainer, separate four-pose sheet generation and visual inspection. No stock-media purchase, API key or new subscription was added.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Poses | Visual loop |
| --- | --- | --- | --- | --- |
| Torso Rotation | `torso-rotation-trainer-source.png` | `trainer_torso_rotation.png` | 4 | 4 seconds |
| Step Jack | `step-jack-trainer-source.png` | `trainer_step_jack.png` | 4 | 4 seconds |
| Standing Knee Drive | `standing-knee-drive-trainer-source.png` | `trainer_standing_knee_drive.png` | 4 | 4 seconds |

Master PNGs are saved beside this document. Runtime PNGs live in `shared/src/commonMain/composeResources/drawable/`, mechanically resized to 1200 × 1200 with `sips`. All sheets are 2 × 2.

Torso Rotation and Standing Knee Drive use cells 0, 0, 1, 2, 3, 3, 2, 1. Step Jack uses 0, 0, 2, 1, 3, 3, 1, 2 because generated cell 2 shows shoulder-height arms, below those in cell 1. All return through the same poses and hold endpoints for one second. Each shows one side/direction and includes a visible reminder to switch. These are presentation-only loops; no workout timing, repetition counting, MVI or persistence changes.

## Review notes

Generated atlases are not rigged models: there is some horizontal supporting-foot/body drift in Standing Knee Drive and Step Jack, and small proportion changes. Floor-baseline alignment does not correct horizontal drift. Step Jack's intermediate arm angles required playback reordering; its stepping and arm movement are not perfectly synchronized. Torso Rotation's final two poses have a small visual difference. These remain trainer previews needing qualified movement review before release; automated playback tests do not certify exercise technique.

Low Impact Conditioning now has seven of nine demonstrations across all four week variants. Low-Impact Skater and Standing Side Crunch remain. Twenty-three of forty catalog exercises have artwork. No physical-device installation or iOS test was performed for this batch.

## Final generation prompts

### torso_rotation

Use case: stylized-concept.
Asset type: HomeFit workout demonstration sprite sheet.
Reference image supplies ONLY trainer identity, outfit and rendering style, not squat poses/layout.
Same adult male trainer with tan skin, short dark hair, fitted moss-green tee, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural anatomy.
EXACT 2 columns × 2 rows grid in a square image, one full person in each equal square cell, four distinct ordered poses top-left, top-right, bottom-left, bottom-right. Fixed camera and figure scale, at least 8% empty margins including fingers/shoes. Opaque solid warm cream #F6F5F0 background, soft floor shadows. No text, labels, lines, arrows, trails, ghost limbs or watermark. Ground baseline y=88% of EACH cell. Body anchor and nonmoving feet stay in identical relative cell coordinates.
Movement: STANDING TORSO ROTATION. Fixed FRONT camera. Feet shoulder-width, planted with soft knees, hips facing camera and mostly quiet. Hands gently crossed on upper chest in all four poses. Four progressive poses rotating upper chest toward trainer's LEFT (image RIGHT): 1 shoulders/chest face forward; 2 turn chest gently about 10 degrees; 3 turn about 20 degrees; 4 comfortable small 30-degree rotation. Head follows chest naturally, no separate neck crank. Both feet, pelvis height and torso center fixed; only upper body turns, no leaning, no deep twisting or arm swinging. Shoulder orientation must change visibly while camera and feet do not. Code will reverse these poses back to neutral and caption says repeat other direction.

### step_jack

Use case: stylized-concept.
Asset type: HomeFit workout demonstration sprite sheet.
Reference image supplies ONLY trainer identity, outfit and rendering style, not squat poses/layout.
Same adult male trainer with tan skin, short dark hair, fitted moss-green tee, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural anatomy.
EXACT 2 columns × 2 rows grid in a square image, one full person in each equal square cell, four distinct ordered poses top-left, top-right, bottom-left, bottom-right. Fixed camera and figure scale, at least 8% empty margins including fingers/shoes. Opaque solid warm cream #F6F5F0 background, soft floor shadows. No text, labels, lines, arrows, trails, ghost limbs or watermark. Ground baseline y=88% of EACH cell. Body anchor and nonmoving feet stay in identical relative cell coordinates.
Movement: LOW-IMPACT STEP JACK, fixed FRONT view. Demonstrate stepping one foot out with both arms raising; NO JUMP. Trainer's LEFT supporting foot (image RIGHT) stays flat in same place in all frames. Trainer's RIGHT foot moves out to image LEFT. Four progressive poses: 1 feet close hip-width, arms relaxed down; 2 right foot halfway stepping sideways with toes near ground and arms raised diagonally about 35 degrees; 3 right foot almost at wide tap position, arms out at shoulder height; 4 right foot planted wide with soft knee, arms comfortably raised in wide overhead V with soft elbows, hands separated. Supporting sole on ground throughout, head height steady, upright torso, no airborne full-body jump or deep squat. Both arms lift symmetrically; no crossed legs. Scale all figures to leave room above overhead hands and beside wide foot. Code reverses to return and caption says alternate sides. Exactly two arms and two legs.

### standing_knee_drive

Use case: stylized-concept.
Asset type: HomeFit workout demonstration sprite sheet.
Reference image supplies ONLY trainer identity, outfit and rendering style, not squat poses/layout.
Same adult male trainer with tan skin, short dark hair, fitted moss-green tee, charcoal shorts, white ankle socks, cream/lime sneakers. Premium stylized 3D illustration, natural anatomy.
EXACT 2 columns × 2 rows grid in a square image, one full person in each equal square cell, four distinct ordered poses top-left, top-right, bottom-left, bottom-right. Fixed camera and figure scale, at least 8% empty margins including fingers/shoes. Opaque solid warm cream #F6F5F0 background, soft floor shadows. No text, labels, lines, arrows, trails, ghost limbs or watermark. Ground baseline y=88% of EACH cell. Body anchor and nonmoving feet stay in identical relative cell coordinates.
Movement: STANDING KNEE DRIVE, fixed THREE-QUARTER SIDE view facing RIGHT, both legs visible. Hands stay lightly on hips, chest tall, no trunk crunch. Demonstrate one knee drive from a small rear toe tap. Trainer's LEFT supporting foot stays flat on same point with softly bent supporting knee. RIGHT moving leg shown clearly in foreground. Four ordered poses: 1 right toes lightly tap floor a short stride behind (image LEFT), supporting leg under torso; 2 right foot leaves rear tap and knee moves under hip; 3 right knee lifted halfway forward; 4 right knee raised comfortably near hip height with thigh forward near horizontal, lower leg hanging down and ankle relaxed. No jump, no high kick, no toe grabbing or backward lean. Keep head/torso/supporting sole stable, two anatomically clear legs, moving knee bends naturally throughout. Code reverses four poses to rear tap; caption says repeat other side.
