# Physical material atlas

Original generated bitmap artwork, created with the built-in image-generation tool on 30 October 2026. This is not a camera photograph or a scanned PBR material set. It is an albedo-detail atlas used on existing native meshes; it does not by itself make those meshes photorealistic.

The selected output was copied into the project, converted to RGB, resized to 1024×1024 and encoded as WEBP quality 90 at `app/src/main/assets/materials/physical_atlas.webp`. `manifest.json` records its hash and quadrants.

## Generation prompt

Use case: product-mockup. Asset type: production texture atlas for a native 3D children's creative game. Create a precisely divided 2 by 2 grid, FOUR EQUAL SQUARE QUADRANTS, each a seamless-looking macro photograph of a physical material, NO gaps, NO grid lines, NO text, NO labels, NO borders, NO objects, NO perspective, orthographic flat material swatches. TOP LEFT pale fine-grained unfinished maple wood with subtle parallel grain. TOP RIGHT warm grey rough natural stone with fine mineral grains, not large cracks. BOTTOM LEFT matte neutral grey moist sculpting clay with delicate microtexture, no fingerprints, no recognizable shape. BOTTOM RIGHT fine neutral grey clean sand with subtle irregular grain, no shells or footprints. Neutral grey luminance throughout so game tint colors can be applied. High resolution fine natural photographic detail, diffuse even lighting, no baked shadows or highlights, no specular shine, photorealistic PBR albedo texture atlas. The four materials exactly fill their quadrants edge to edge. This is an actual material bitmap, not a UI mockup. Avoid cartoon, stylized illustration, plastic, saturated colors, invented logos or watermark.
