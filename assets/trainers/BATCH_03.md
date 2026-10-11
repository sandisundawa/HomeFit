# Trainer batch 03: Full Body Foundation cooldown

Created with the built-in image-generation tool, using `squat-trainer-source.png` as a character/style reference. The image-generation skill guided matching the trainer, inspecting outputs and saving selected artwork in the workspace. No paid stock assets, API key or new subscription was added.

## Deliverables

| Exercise | Source | Bundled drawable | Distinct frames |
| --- | --- | --- | --- |
| Hamstring Stretch | `hamstring-stretch-trainer-source.png` | `trainer_hamstring_stretch.png` | 4 |
| Deep Breathing | `deep-breathing-trainer-source.png` | `trainer_deep_breathing.png` | 4 |

Sources are preserved here. Runtime PNGs in `shared/src/commonMain/composeResources/drawable/` are mechanically resized with `sips` to 1200 × 1200, with 2 × 2 frame layouts.

Hamstring Stretch is a single-side standing heel-forward setup demo. Its 12-second visual loop holds the final pose for six seconds and includes a switch-side reminder. It is not a prescribed stretch duration and does not change the workout timer. Deep Breathing uses subtle chest/abdomen changes in an eight-second visual loop; captions indicate gentle inhalation for the first two frames and gentle exhalation for the last two. There is no breath-hold phase. The detail screen tells users to breathe comfortably at their own pace.

All nine exercises in Full Body Foundation now map to trainer artwork. These remain illustrative AI-generated previews requiring movement review before release, not a professionally certified exercise program.

## Final prompts

### hamstring_stretch

Use case: stylized-concept. Original HomeFit exercise sprite sheet. Reference is CHARACTER / STYLE ONLY. Match same adult male trainer, tan skin, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers, premium stylized 3D illustration with natural anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells, ONE full person per cell. Constant scale, camera, outfit, ground y=88% each cell. Full head, fingertips and shoes visible with 10% margins. OPAQUE warm cream #F6F5F0 background, no transparency, words, arrows, gridlines or shadows. Four poses read left-right then top-bottom. Exercise STANDING HAMSTRING STRETCH, strict SIDE VIEW facing right. Demonstrate ONE SIDE. Front leg slightly extended with HEEL planted ahead and toes gently pointing UP, front knee almost straight but not locked. Rear supporting knee softly bent with rear foot flat. Hands resting on rear thigh ABOVE knee, never pushing front knee. Exactly four distinct progressive setup poses: 1 upright torso, heel already forward and toes up; 2 small hip hinge keeping back long and hips back; 3 medium comfortable hip hinge; 4 modest final hinge, chest angled about 35 degrees forward from upright, neck aligned with back, no rounding or reaching to toes. Hips shift backward, rear knee softens slightly; BOTH shoe contact points fixed. No deep forward fold, no bouncing, no lunge, no floor stretch. Code will hold last pose for several seconds then ease out.

### deep_breathing

Use case: stylized-concept. Original HomeFit exercise sprite sheet. Reference is CHARACTER / STYLE ONLY. Match same adult male trainer, tan skin, short dark hair, moss fitted T-shirt, charcoal shorts, white ankle socks, cream/lime sneakers, premium stylized 3D illustration with natural anatomy. NEW square sheet EXACTLY 2 columns x 2 rows, four equal square cells, ONE full person per cell. Constant scale, camera, outfit, ground y=88% each cell. Full head, fingertips and shoes visible with 10% margins. OPAQUE warm cream #F6F5F0 background, no transparency, words, arrows, gridlines or shadows. Four poses read left-right then top-bottom. Exercise GENTLE DEEP BREATHING while STANDING, front three-quarter view. Feet hip width planted, shoulders down, head upright, eyes softly relaxed, right hand on upper chest and left palm on abdomen. Hands stay in contact, no arms raised overhead. Four subtly DISTINCT consecutive breathing phases: 1 beginning gentle inhale, abdomen neutral; 2 fuller comfortable inhale, abdomen and lower hand move slightly outward, chest gently expands without lifting shoulders; 3 gentle exhale in progress, mouth softly pursed and abdomen partly relaxed; 4 end comfortable exhale, lips relaxed and abdomen returned neutral. No forceful inhalation, no breath holding, no exaggerated belly inflation or backward lean. Same hand placement, body position and scale in every cell. Visible but subtle hand/abdomen/chest changes. Code will add inhale/exhale cues outside the image, so render NO text.
