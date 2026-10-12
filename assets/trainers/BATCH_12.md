# Trainer batch 12: Donkey Kick, Front Plank, Modified Side Plank

Created with the built-in image-generation tool using the squat source as a character/style reference. No API fallback, API key, new dependency or subscription. Ponytail kept this batch within the existing artwork mapping, sequence tests and documentation.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Loop |
| --- | --- | --- | --- |
| Donkey Kick | `donkey-kick-trainer-source.png` | `trainer_donkey_kick.png` | 4 seconds |
| Front Plank | `front-plank-trainer-source.png` | `trainer_front_plank.png` | 12 seconds |
| Modified Side Plank | `modified-side-plank-trainer-source.png` | `trainer_modified_side_plank.png` | 12 seconds |

Master PNGs live beside this document; runtime PNGs live in `shared/src/commonMain/composeResources/drawable/`. Each is a four-pose 2 × 2 sheet mechanically resized with sips to 1200 × 1200. Second-row crop origins: 600, 543 and 592 respectively, aligning mat baselines.

Donkey Kick uses 0, 0, 1, 2, 3, 3, 2, 1 for lifting and returning. Both planks use 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1, 0: setup, six-second visual hold, release. Visual hold duration is not an exercise prescription or workout timer. Donkey Kick and Modified Side Plank show one side with switch-side reminders. No MVI, persistence, timer or repetition-counting changes.

## Review limitations

AI artwork has small anchor/proportion changes and some upward gaze rather than a fully neutral neck; qualified movement review is required before release. Front Plank's final two poses are deliberately similar settled holds. Automated tests verify playback and crops, not anatomical correctness. No phone installation or iOS verification was requested.

Coverage: 34 of 40 exercises. Remaining: Superman, Fast Feet Tap, Heel Touch, Seated Knee Tuck, Cat-Cow, Child's Pose.

## Generation prompts

### donkey_kick

Use case: stylized-concept. HomeFit workout sprite atlas. Reference image is ONLY trainer identity/style, not squat poses or layout. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D anatomy. NEW square image EXACTLY 2 columns x 2 rows, FOUR equal square cells read left-right top-bottom. Opaque warm cream #F6F5F0 background, thin moss exercise mat, soft shadows. Same fixed camera, full-body scale and mat position throughout. Full person, shoes and fingers within each cell, 10% margins. No text, arrows, gridlines, extra limbs or equipment. Mat ground baseline at 82% height of EACH cell. DONKEY KICK, fixed three-quarter side view facing RIGHT. Both palms planted beneath shoulders, left supporting knee under hip fixed throughout. Near/right knee remains bent 90 degrees while lifting behind the body; torso stays parallel to mat, hips square, neutral spine and neck. Four progressive poses: 1 all fours both knees on mat; 2 near/right knee lifts a little behind; 3 near/right thigh halfway back/up; 4 near/right thigh aligned with torso, bent lower leg points up and shoe sole faces ceiling. No lifting thigh higher than hip, no back arch, no straight leg, no moving hands or grounded knee. Identical hand and supporting-knee coordinates in every cell; only right hip/leg moves. Code reverses four poses for return, one side only.

### front_plank

Use case: stylized-concept. HomeFit workout sprite atlas. Reference image is ONLY trainer identity/style, not squat poses or layout. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D anatomy. NEW square image EXACTLY 2 columns x 2 rows, FOUR equal square cells read left-right top-bottom. Opaque warm cream #F6F5F0 background, thin moss exercise mat, soft shadows. Same fixed camera, full-body scale and mat position throughout. Full person, shoes and fingers within each cell, 10% margins. No text, arrows, gridlines, extra limbs or equipment. Mat ground baseline at 82% height of EACH cell. FRONT FOREARM PLANK, fixed slightly elevated SIDE camera, head RIGHT feet LEFT. Elbows directly beneath shoulders, forearms parallel resting on mat, toes tucked. Show four controlled SETUP poses toward a hold: 1 knees supported with forearms placed and feet tucked behind; 2 extend near leg back with toes planted while far knee remains supported; 3 extend far leg back also, lifting knees into a straight forearm plank; 4 settled full forearm plank, head-shoulders-hips-heels in one line with a subtle final weight adjustment. Final pose both knees OFF mat, legs straight, toes and forearms support body. Do not show high push-up, hip pike, sagging belly, back arch or forearms lifting. Forearms pinned to exactly same mat coordinates throughout. These are setup then HOLD frames, not push-up repetitions.

### modified_side_plank

Use case: stylized-concept. HomeFit workout sprite atlas. Reference image is ONLY trainer identity/style, not squat poses or layout. Same tan adult male, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D anatomy. NEW square image EXACTLY 2 columns x 2 rows, FOUR equal square cells read left-right top-bottom. Opaque warm cream #F6F5F0 background, thin moss exercise mat, soft shadows. Same fixed camera, full-body scale and mat position throughout. Full person, shoes and fingers within each cell, 10% margins. No text, arrows, gridlines, extra limbs or equipment. Mat ground baseline at 82% height of EACH cell. MODIFIED SIDE PLANK ON KNEES, fixed front-side three-quarter view, head LEFT feet RIGHT, torso facing viewer. Support LEFT forearm with elbow directly below left shoulder, forearm extending forward on mat. Both knees bent around 90 degrees, stacked lower knees stay on mat, lower legs extend behind toward right. RIGHT hand stays on upper hip. Four progressive SETUP poses: 1 side lying with left forearm placed and hip resting on mat; 2 gently lift hip a small amount; 3 hip midway to alignment; 4 full supported side plank with head, shoulders, hips and KNEES in one straight line, pelvis lifted, lower left knee and forearm grounded. Shoulders and hips stacked, no twisting, neutral neck. Both knees remain BENT and supported, not straight-leg side plank. Forearm and knee anchor positions identical in all cells. Code holds last pose before gently reversing to lower; demonstrate one side only.
