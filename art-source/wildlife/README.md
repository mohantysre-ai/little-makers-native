# Original story wildlife surfaces

`WildlifeGeometry.java` is the editable source for the rabbit, fox and bear meshes. No downloaded model, proprietary character, generated bitmap, or external asset service was used. The geometry is built from original anatomical surface assemblies with explicit feet, muzzles, ears, eyes and rabbit whiskers. Coordinates follow the existing story board: positive Y points down, Z provides depth, and one model unit is scaled by its scene placement.

`WildlifeRenderer.java` uploads only species which are actually used, caches the immutable CPU mesh, and draws each animal in one VBO draw. The original coat shader adds fine surface variation, restrained rim light, eye highlights and GPU breathing with a corrected normal. GL context recreation rebuilds GPU resources. NativeStageSurface restores its own program, attribute state and buffer binding after each wildlife draw.

This is a procedural visual upgrade. It is not a scanned animal, strand-based fur simulation, a production skeletal locomotion rig or certification of photorealism. Other story species and many environment props retain their earlier stylized meshes.

Host tests check bounded finite coordinates, normals, surface/color values, species coverage and cache reuse. Device tests render the rabbit, fox and bear, verify shader availability and check GL errors. No physical-device frame-rate certification is claimed.
