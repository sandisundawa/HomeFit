# Human trainer artwork

Created with the built-in image-generation tool. The skill guided generation, visual inspection, refinement and saving the selected artwork into the project.

Source: `squat-trainer-source.png`. Runtime atlas: `../../shared/src/commonMain/composeResources/drawable/trainer_squat.png`, resized mechanically with macOS `sips -z 1200 1200`.

This is original illustrative artwork, not a downloaded human model. Only five descent poses are currently selected for the app's squat preview; see `../../docs/EXERCISE_ANIMATIONS.md` for limitations.

## Generation prompt

Use case: stylized-concept.
Asset type: production animation sprite sheet for HomeFit exercise app.
Create ONE square sprite sheet, exactly 4 columns by 4 rows, 16 equally sized square cells, no gaps, no dividers. Each cell is one full-body frame of the SAME handsome adult male gym instructor, athletic but naturally proportioned, warm medium tan skin, short dark textured hair, clear simplified face, moss green fitted short-sleeved workout shirt, charcoal athletic shorts, white ankle socks and cream/lime trainers. Premium stylized 3D-rendered mobile-game character, natural anatomy, soft studio lighting, gently sculpted muscles, detailed hands and shoes, NOT stick figure, NOT flat pictogram, NOT photorealistic. Entire background uniform pale warm cream #F6F5F0, no text, no labels, no shadows spanning cells.
Camera locked strict side view, character facing right, whole body fully visible with 12% margins in every cell. Constant scale and identical character in all cells; shoes stay at same horizontal position and floor height 88% down each cell.
The 16 frames read left-to-right top-to-bottom and depict a single smooth bodyweight squat repetition: frame 1 tall standing arms forward; frames 2-7 progressively lower hips back and down with knees bending and heels planted, knees track toes, neutral straight back; frame 8 and 9 lowest squat thighs near parallel; frames 10-15 progressively stand up reversing the same poses; frame16 matches frame1. Both arms held forward throughout. Motion must be meaningfully different across consecutive frames, consistent skeletal proportions and clothing, feet never slide. Render sixteen distinct frames, one person per cell, no repeated full rows, no camera changes.

## Final refinement prompt

Edit this 4x4 squat animation sheet only to clean its background and framing. Keep the SAME trainer, clothing, side view and sixteen squat poses, their order and shading. Replace every background pixel and all colored edge noise with solid opaque warm cream #F6F5F0. NO transparency, NO glow, NO black background, NO floor shadows. Exact 4x4 uniform grid with square cells and NO lines or gutters. Fit each full person entirely inside their cell with 8 percent margin minimum at top and sides; align soles to 88 percent of each cell height. Consistent person scale and foot anchor position in all sixteen cells. Make the first and last standing pose identical and fully inside the cell. Keep natural detailed human features. Deliver a clean opaque square sprite atlas.
