# Shulker Highlighter 0.2.1 — Mod Menu diagnostics fix

Requires Minecraft **26.3**, Fabric Loader **0.19.5+**, **Fabric API** compatible with 26.3, and **Mod Menu 21.0.0**.

## Changes from 0.2.0

- Uses official Terraformers Maven dependency `com.terraformersmc:modmenu:21.0.0`.
- Makes Mod Menu a required dependency. Instead of silently lacking Configure when Mod Menu is absent, Fabric Loader will report the missing/incompatible Mod Menu version.
- GitHub workflow inspects the built JAR's `fabric.mod.json`, checks the `modmenu` entrypoint, and checks compiled settings-screen classes.
- These changes **do not prove the renderer works in game**.

## Install

1. GitHub Actions green run → download **shulker-highlighter-0.2.1** artifact → unzip.
2. Put only **shulker-highlighter-0.2.1.jar** in the instance's `mods` directory. Delete old shulker-highlighter 0.1/0.2 JARs and do not install a `-sources.jar`.
3. Install `modmenu-21.0.0.jar` and 26.3-compatible Fabric API in **the same** `mods` directory.
4. Start Fabric profile **26.3**. Open Mods → Shulker Highlighter → gear/Configure.
5. If it fails, share `.minecraft/logs/latest.log` (or the launcher instance's logs/latest.log) and what happens: missing from list / no Configure button / clicking crashes / settings appear but no highlight.

## Important

Correct registration does not guarantee Minecraft in-game rendering. If the 0.2.0 JAR is *already* visible with a Configure button, the issue may be the renderer instead of Mod Menu. This patch does not solve a renderer issue. 
