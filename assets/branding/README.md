# HomeFit icon

Original artwork created with the built-in image-generation tool. The mark combines a home roof and a person stretching; the palette follows HomeFit's lime and forest theme.

- Master artwork: `homefit-icon.png`.
- Android: density-specific legacy icons and adaptive foregrounds in `appAndroid/src/main/res/mipmap-*`. Android 13+ also has a matching vector monochrome mark for themed launchers. The opaque foreground includes generous background margins for launcher masks.
- iOS: opaque 1024px artwork in `iosApp/HomeFit/Assets.xcassets/AppIcon.appiconset`, selected by `iosApp/project.yml`. Regenerate the Xcode project with XcodeGen when needed.

Platform PNGs are resized from the master using macOS `sips`. Android legacy sizes: 48, 72, 96, 144, 192px; adaptive canvases: 108, 162, 216, 324, 432px.

## Final refinement prompt

Refine this HomeFit launcher icon. Keep the same centered house roof and exercising person silhouette and generous margins. Remove ALL glow, noise, texture, outlines, shading, gradients and stray pixels. Solid flat lime #D9F074 emblem on a completely opaque, uniform deep forest #17221D square background extending to all edges. Clean professional flat logo with crisp smooth boundaries. No transparency. No text. No rounded tile or mockup. Emblem occupies at most 58 percent of canvas width and height. Return one square production icon.
