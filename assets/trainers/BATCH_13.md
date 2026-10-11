# Trainer batch 13: Superman, Fast Feet Tap, Heel Touch

Generated with the built-in image-generation tool, using the squat source for trainer identity and style. Fast Feet Tap received a targeted edit to alternate the raised foot; its first draft repeated one side. No API fallback, new dependency, API key or subscription.

## Assets and playback

| Exercise | Source beside this file | Runtime drawable | Loop |
| --- | --- | --- | --- |
| Superman | `superman-trainer-source.png` | `trainer_superman.png` | 4 seconds |
| Fast Feet Tap | `fast-feet-tap-trainer-source.png` | `trainer_fast_feet_tap.png` | 2 seconds |
| Heel Touch | `heel-touch-trainer-source.png` | `trainer_heel_touch.png` | 4 seconds |

Runtime PNGs are in `shared/src/commonMain/composeResources/drawable/`, mechanically resized with sips to 1200 × 1200. All are four-pose 2 × 2 atlases. Second-row crop offsets: Superman 560, Fast Feet Tap 583, Heel Touch 598.

Superman reverses its lift with sequence 0, 0, 1, 2, 3, 3, 2, 1. The alternating exercises cycle 0, 1, 2, 3. Fast Feet Tap is slowed for a readable demonstration, not prescribed exercise cadence. Existing rendering and pause/resume behavior is reused; no timer, MVI or persistence changes.

## Review limitations

Generated artwork has small position/proportion drift. Fast Feet Tap's final two contact poses are similar; Heel Touch has some head rotation and an exaggerated shoulder lift. These are previews requiring qualified movement review before release, not validated technique instruction. Automated tests check rendering, sequences and crop bounds rather than biomechanics. No phone installation or iOS verification was requested.

Coverage: 37 of 40. Remaining: Seated Knee Tuck, Cat-Cow and Child's Pose.

## Generation prompts

### superman

Use case: stylized-concept. HomeFit workout animation atlas. Input image is trainer identity/style REFERENCE ONLY. Same tan male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks and cream/lime shoes, polished stylized 3D natural anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Locked camera, scale and proportions throughout. Opaque cream #F6F5F0 background, soft shadows. Whole body with 10% margins each cell. No text, gridlines, arrows or ghost limbs. SUPERMAN floor exercise, NOT superhero. Fixed slightly elevated SIDE view, head RIGHT feet LEFT, thin moss mat. Lying prone, arms reaching forward shoulder-width, legs extended hip-width; abdomen and pelvis remain on mat. Four progressive poses: 1 arms and legs relaxed on mat, face looking down; 2 gently lift hands and feet a tiny distance; 3 modestly lift chest/arms/legs; 4 gentle final lift only a few inches, long neutral neck looking DOWN at mat, no exaggerated back arch, no bent knees, no throwing head up. Code reverses for lowering. Keep pelvis position fixed at x50%, mat baseline y80% in each cell. Clearly show both arms reaching forward, both legs back, all fingers and shoes inside cell.

### fast_feet_tap

Use case: stylized-concept. HomeFit workout animation atlas. Input image is trainer identity/style REFERENCE ONLY. Same tan male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks and cream/lime shoes, polished stylized 3D natural anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Locked camera, scale and proportions throughout. Opaque cream #F6F5F0 background, soft shadows. Whole body with 10% margins each cell. No text, gridlines, arrows or ghost limbs. FAST FEET TAP in place. Fixed FRONT view, full standing trainer centered x50%, ground baseline y88%. Athletic stance feet hip width, softly bent knees, small forward lean from hips, elbows bent at sides. Four tiny alternating foot-contact phases: 1 left shoe lifted 2cm, right foot planted; 2 left forefoot touches ground while right heel begins lifting; 3 right shoe lifted 2cm, left foot planted; 4 right forefoot touches while left heel begins lifting. Body stays at constant height, no high knees, no squat, no jumping with both feet airborne, no moving across floor. Keep feet under hips, arms make small opposite-leg rhythmic movement. Four distinct readable foot positions, consistent face/outfit/camera.

### heel_touch

Use case: stylized-concept. HomeFit workout animation atlas. Input image is trainer identity/style REFERENCE ONLY. Same tan male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks and cream/lime shoes, polished stylized 3D natural anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells read left-right top-bottom. Locked camera, scale and proportions throughout. Opaque cream #F6F5F0 background, soft shadows. Whole body with 10% margins each cell. No text, gridlines, arrows or ghost limbs. ALTERNATING HEEL TOUCH floor exercise. Fixed elevated FRONT view from foot end, enough elevation to see both hands reaching beside torso. Thin moss mat, supine with knees bent and both feet planted hip-width close to buttocks. Shoulder blades lifted just slightly, neck neutral, eyes toward knees. Four poses: 1 centered shallow crunch with arms long at sides, fingertips near heels; 2 small side bend reaching RIGHT hand toward outside of RIGHT heel, left hand moves away from its heel; 3 return center with small weight transfer; 4 small side bend LEFT hand toward outside LEFT heel, right hand moves away. Pelvis and both feet stay fixed in EXACT same cell-relative coordinates. Shoulders never lift into sitting, do not grab knees, no reaching across body, feet never lift, no twisting. Full head, both hands and shoes visible without leg occlusion, mat baseline y82% each cell. Code cycles these four poses.

## Fast Feet Tap refinement

Edit this exact 2x2 Fast Feet Tap trainer atlas. Keep top two cells unchanged. Correct ONLY the bottom two cells to demonstrate the OPPOSITE FOOT: mirror the BODY POSE left-to-right within each bottom cell, so the shoe on image LEFT is lifted in bottom-left cell and image RIGHT shoe is planted; bottom-right image LEFT forefoot comes down as image RIGHT heel lifts. Reverse arm swing to match opposite foot. Preserve identity, outfit, camera, scale, background, four-cell layout and floor baseline. No text, borders or extra limbs. The upper row lifts shoe on image RIGHT; lower row MUST lift shoe on image LEFT. This is alternating tiny taps in place, not high knees.
