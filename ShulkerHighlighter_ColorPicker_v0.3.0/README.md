# Shulker Highlighter 0.3.0 (Minecraft 26.3)

Client-side Fabric mod with Mod Menu GUI for selecting any combination of all 17 original shulker colors, and choosing highlight hue from 16 palette swatches or a custom #RRGGBB value.

## Usage
In Mod Menu click Shulker Highlighter > Configure. Each [x] toggles one original dye; multiple may be active. Click a colored square to set highlight hue or enter a hexadecimal value. Choose Recolor or Outline. Press Save & Done. `Select ALL` includes undyed.

## Building
Upload the **contents** of this project to GitHub repository root. GitHub Actions compiles it and checks classes and fabric.mod.json. Download the resulting artifact ZIP and install its normal JAR (not -sources.jar) into the Prism Launcher instance Mods tab.

## Limitations
The existing mixin targets vanilla ShulkerBoxRenderer. Other block-entity rendering mods (e.g. Better Block Entities) may intercept closed-box rendering. The expanded outline is a proposed fix but has not been tested in-game; depth and placement should be tested with BBE disabled first. This project's source was inspected and packaged, not compiled in this container (Java 25/Gradle unavailable). A GitHub green check indicates compilation and packaging, not a verified in-game behavior.
