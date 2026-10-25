# Shulker Highlighter — development scaffold

Minecraft 26.3, Fabric Loader 0.19.5, optional Mod Menu 21.0.0.

## IMPORTANT STATUS
This is **NOT YET A WORKING HIGHLIGHT MOD**. It includes persistent settings and a Mod Menu screen, but **the world rendering implementation is not yet present**. It has not been compiled or tested. Do not describe its resulting JAR as implementing highlighting or recoloring. Minecraft 26.3 changed the rendering API and a tested integration is still required.

## Build online
1. Create a GitHub repository and upload the CONTENTS of this folder, preserving `.github/workflows/build.yml`.
2. In the Actions tab, run `Build Fabric Mod` (or push a commit).
3. Check the logs. If the build succeeds, download `shulker-highlighter-build` from the workflow run's Artifacts section.
4. This build is only a settings scaffold until rendering support is implemented and tested.

## Settings
Target shulker color, outline/recolor mode, and highlight color palette are saved to `config/shulker-highlighter.properties`.

## Implementation needed
Add a 26.3-compatible world rendering hook to draw depth-tested outlines for all visible matching shulker block entities, and a compatible model tint/recolor path. Verify occlusion against opaque walls, compatibility with Sodium, and normal block-entity animation. Only then release a functional mod.
