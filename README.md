# Shulker Highlighter — Minecraft 26.3 build-corrected scaffold

## IMPORTANT STATUS — READ BEFORE USING

This is a **development scaffold**. It has a configurable Mod Menu screen and saves target colour / mode / RGB settings. **It does not yet implement the world-rendering/highlighting effect.** A successfully compiled JAR is **not** the requested finished mod. Do not release this as the finished highlighter.

## Correct build stack

- Minecraft Java 26.3
- Fabric Loader 0.19.5
- Non-obfuscated Loom `net.fabricmc.fabric-loom` 1.18.2
- Java 25, Gradle 9.6.0
- Fabric API 0.163.0+26.3
- Optional Mod Menu 21.0.0
- No mappings line; use `implementation` / `compileOnly` instead of legacy `modImplementation`.

## GitHub Upload

Extract this ZIP, then upload the files and folders *inside* the extracted folder to the **repository root**, not into another `shulker-highlighter` subfolder. GitHub should show `build.gradle`, `settings.gradle`, `src`, and `.github` at root.

The Actions workflow is `.github/workflows/build.yml`. The workflow checks for Java source files, builds with a pinned Gradle 9.6.0 on Java 25, inspects the built JAR for required files, and uploads only the installation JAR under the name `shulker-highlighter-dev-jar`.

## NOT IMPLEMENTED

- Drawing actual block outlines in-world
- Full bright recoloring of shulker block model
- Through-wall occlusion test and Sodium compatibility
- Actual compilation and Minecraft client launch test (build environment was offline)

## Sources used for the correction

- https://docs.fabricmc.net/develop/loom/
- https://github.com/FabricMC/fabric-example-mod/tree/26.3
- https://modrinth.com/mod/modmenu/version/21.0.0
- https://www.fabricmc.net/2026/09/15/263.html
