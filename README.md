# Shulker Highlighter v0.2.0 — Fabric, Minecraft 26.3

## What this build is intended to do

- Client-side, on Minecraft Java 26.3 / Fabric Loader 0.19.5, Java 25.
- In Mod Menu: **Shulker Highlighter → Configure**.
- Set **Enabled**, the original **shulker box dye**, and **Effect** (Outline or Recolor).
- Set any 6-digit RGB hexadecimal color such as `#FF0000`, or press **Next preset color**.
- **Outline:** depth-tested line box around visible shulker blocks. It should not show through walls.
- **Recolor:** replaces the shulker's texture with vanilla white and tints it with your chosen RGB, preserving texturing. It does not permanently change the shulker dye or stored data.
- Click **Save & Done** to apply and persist; **Cancel/Escape** discards unsaved changes.
- Client preferences stored in `config/shulker-highlighter.properties`.

## Limitations

- Applies to **placed shulker boxes**, not shulker items in inventories or hotbars.
- Outline is a bounding outline, not a blur/bloom effect. It does not follow an opened lid.
- Custom texture packs can affect the final recolor because the white-shulker texture is RGB-multiplied.
- Rendering is intended to respect wall depth. Shader mod interactions are not yet tested.

## Build (online, no local Gradle required)

Upload the **contents** of this ZIP to the root of your GitHub repository, including the `src` and `.github` folders. Under **Actions → Build Fabric Mod** inspect newest run. A green check indicates Gradle compilation + packaging checks passed. Download the `shulker-highlighter-0.2.0` artifact and **extract** the installable `.jar`. Place just the JAR in `%appdata%\.minecraft\mods` alongside Fabric API, Mod Menu and its dependencies. Remove any previous Shulker Highlighter JAR. Launch Minecraft 26.3 with Fabric.

The GitHub job compiles and verifies the classes, but **cannot certify actual visual behavior**: use a test world and place a green shulker box with an opaque wall behind it, test each effect in Mod Menu, and verify the overlay disappears when the shulker is fully occluded.

## Build specifications

- Non-obfuscated Loom 1.18.2 (plugin `net.fabricmc.fabric-loom`)
- Gradle 9.6, JDK 25
- Fabric API 0.163.0+26.3
- Optional Mod Menu 21.0.0 compile-only dependency
- Uses a Mixin on Minecraft 26.3's `ShulkerBoxRenderer`; no remote server changes.

## References used for the target API

- https://github.com/FabricMC/fabric-example-mod/blob/26.3/build.gradle
- https://mc-packet-reference.netlify.app/26.x/source/net/minecraft/client/renderer/blockentity/shulkerboxrenderer/
- https://mc-packet-reference.netlify.app/26.x/source/net/minecraft/client/renderer/orderedsubmitnodecollector/
- https://modrinth.com/mod/modmenu
