# Shulker Highlighter 0.4.0 — Mod Menu color controls

Minecraft 26.3 / Fabric 0.19.5 / Mod Menu 21.0.0.

## What changed

The configuration UI has THREE separate pages: Settings, Shulker Colors (17 independently selectable source colors), and Highlight Color (16 visibly color-coded choices with names, plus editable hex RGB). The choice is saved only after returning to Settings and pressing Save & Done. The old Next Preset button is gone. The shulker renderer mixin is retained.

## Important rendering limitation

Better Block Entities (BBE) renders **closed** shulker boxes through its static terrain mesh rather than vanilla ShulkerBoxRenderer, so this mod's recolor and outline rendering is bypassed while a shulker is closed with BBE shulker optimization enabled. Disable BBE's **shulker box optimization** in its configuration to allow the vanilla shulker renderer to run, or temporarily disable BBE when testing. This project does **not** yet provide fully verified BBE-compatible closed-shulker rendering or confirmed visible outline in the default modpack. The new GUI does not by itself fix those rendering issues.

## GitHub upload

Upload the *contents* of the ZIP to the root of the repository, including `src/main/java` and `src/main/resources`, replacing existing files, and commit. The updated GitHub Actions job checks both classes and the metadata in the built JAR before providing an artifact. Install only `shulker-highlighter-0.4.0.jar` from the artifact, not `-sources.jar`.

## Validation

Source-file structure and JSON validated; the full Gradle compilation and in-game test have not been performed in this environment.
