# Trainer batch 11: Jumping Jack, Bridge March, Clamshell

Created using the built-in image-generation tool, with the squat artwork as character/style reference. The image-generation skill guided separate four-pose sheets and visual review; Ponytail kept integration within the existing animation mapping and tests. No new dependency, API fallback, subscription or API key.

## Deliverables

| Exercise | Source artwork | Runtime drawable | Loop |
| --- | --- | --- | --- |
| Jumping Jack | `jumping-jack-trainer-source.png` | `trainer_jumping_jack.png` | 3.2 seconds |
| Bridge March | `bridge-march-trainer-source.png` | `trainer_bridge_march.png` | 4.8 seconds |
| Clamshell | `clamshell-trainer-source.png` | `trainer_clamshell.png` | 4 seconds |

Sources live beside this document. Runtime files are in `shared/src/commonMain/composeResources/drawable/`, mechanically resized with sips to 1200 × 1200. Each sheet has four cells in a 2 × 2 layout. Jumping Jack and Bridge March play 0, 1, 2, 3. Clamshell plays 0, 0, 1, 2, 3, 3, 2, 1, with endpoint holds and reversed closing. Second-row crop origins are 600, 550 and 576 respectively.

Bridge March alternates legs with hips raised; both-feet-down poses are intentionally similar. Clamshell demonstrates one side with a switch reminder. The animation clock remains presentation-only; no workout timer, repetition counting, MVI or persistence changes.

## Review limits

Generated poses have small position/proportion changes between cells; Clamshell includes faint sheet seams. These are stylized previews, not validated technique instruction. Qualified movement review is still needed before release. Automated checks validate playback, crop boundaries and pause/resume, not biomechanics. No physical-device or iOS verification is included in this batch.

Coverage after integration: 31 of 40 exercises; 9 remain. All four existing workout routines remain fully covered.

## Generation prompts

### jumping_jack

Use case: stylized-concept. Asset: HomeFit workout animation sprite atlas. Input image is CHARACTER AND STYLE REFERENCE ONLY, not pose/layout. Same tan adult male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D appearance with natural anatomy. Create NEW square opaque warm cream #F6F5F0 sheet, EXACTLY 2 columns x 2 rows of equal square cells. Four distinct poses reading left-right top-bottom. Same scale, locked camera and body proportions in every cell, entire person inside each cell with generous 10% margins. No text, borders, arrows, ghost limbs, extra people or equipment. JUMPING JACK: frontal view, one full jump-out/jump-in cycle. 1 feet together, arms relaxed beside hips, soft knees on ground. 2 airborne opening with feet moving apart and arms rising diagonally to shoulder height, shoes visibly just above ground. 3 wide stance soft landing with arms overhead slightly bent, hands nearly together. 4 airborne closing with feet returning inward and arms descending diagonally. Keep centerline at x50% each cell, standing ground y88%, head/body size constant; overhead hands completely within frame. Both legs always move symmetrically. Four clearly different silhouettes. Not a step jack, no one-legged stance.

### bridge_march

Use case: stylized-concept. Asset: HomeFit workout animation sprite atlas. Input image is CHARACTER AND STYLE REFERENCE ONLY, not pose/layout. Same tan adult male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D appearance with natural anatomy. Create NEW square opaque warm cream #F6F5F0 sheet, EXACTLY 2 columns x 2 rows of equal square cells. Four distinct poses reading left-right top-bottom. Same scale, locked camera and body proportions in every cell, entire person inside each cell with generous 10% margins. No text, borders, arrows, ghost limbs, extra people or equipment. BRIDGE MARCH: elevated three-quarter SIDE view, head on image LEFT feet image RIGHT on thin moss mat. Supine bridge with shoulders/head and palms on mat, hips lifted in neutral shoulder-hip-knee line throughout all FOUR poses, no back arch. 1 both feet planted hip width, knees bent around 90 degrees. 2 keep FAR foot planted and raise NEAR bent knee toward chest, shin roughly horizontal, knee remains bent. 3 both feet back on mat maintaining raised hips; a distinct small controlled weight-transfer pose. 4 keep NEAR foot planted and lift FAR bent knee toward chest. Show alternating legs unambiguously with slight camera elevation and hip-width separation. Shoulders, head, hands and mat occupy IDENTICAL cell-relative coordinates throughout, body does not slide. No straight leg extension, no seated position, no lowering hips to floor. Mat ground baseline y82% each cell.

### clamshell

Use case: stylized-concept. Asset: HomeFit workout animation sprite atlas. Input image is CHARACTER AND STYLE REFERENCE ONLY, not pose/layout. Same tan adult male trainer, short dark hair, moss fitted shirt, charcoal shorts, white socks, cream/lime shoes, polished stylized 3D appearance with natural anatomy. Create NEW square opaque warm cream #F6F5F0 sheet, EXACTLY 2 columns x 2 rows of equal square cells. Four distinct poses reading left-right top-bottom. Same scale, locked camera and body proportions in every cell, entire person inside each cell with generous 10% margins. No text, borders, arrows, ghost limbs, extra people or equipment. CLAMSHELL: front-side view of trainer lying on LEFT side on thin moss mat, head image LEFT feet image RIGHT. Left arm along ground supports head; right hand on upper hip, shoulders and hips stacked, both knees bent about 70 degrees with feet behind knees. Heels remain together at identical cell coordinates in ALL poses; lower knee, pelvis, head and trunk stay still. Only upper RIGHT knee rotates upward. 1 knees together closed. 2 upper knee opens 10 degrees. 3 opens 20 degrees. 4 opens a comfortable 35 degrees, feet STILL TOUCHING, pelvis never rolls backward. Distinguish from straight leg raise: both knees remain BENT and heels joined. Four progressively opening poses, code reverses to close. Identical camera and full-body size across cells. Mat baseline y82% each cell.
