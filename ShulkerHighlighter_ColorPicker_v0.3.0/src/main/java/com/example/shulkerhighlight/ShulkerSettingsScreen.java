package com.example.shulkerhighlight;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Mod Menu configuration with multi-target selection and clickable RGB palette. */
public final class ShulkerSettingsScreen extends Screen {
    private static final int[] PALETTE = {
        0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00,
        0x00AA00, 0x00FFFF, 0x007FFF, 0x0000FF,
        0x7F00FF, 0xFF00FF, 0xFF69B4, 0xFFFFFF,
        0xC0C0C0, 0x808080, 0x8B4513, 0x000000
    };
    private final Screen parent;
    private boolean active;
    private String selectedMode;
    private final Set<String> selectedTargets = new LinkedHashSet<>();
    private int selectedRgb;
    private EditBox hexBox;
    private Button saveButton;

    public ShulkerSettingsScreen(Screen parent) {
        super(Component.literal("Shulker Highlighter"));
        this.parent = parent;
        this.active = ShulkerHighlighter.enabled;
        this.selectedTargets.addAll(ShulkerHighlighter.targets);
        this.selectedMode = ShulkerHighlighter.mode;
        this.selectedRgb = ShulkerHighlighter.rgb;
    }

    @Override
    protected void init() {
        int buttonWidth = 98;
        int gap = 4;
        int totalWidth = buttonWidth * 4 + gap * 3;
        int x = (this.width - totalWidth) / 2;
        int top = Math.max(18, (this.height - 420) / 2);

        addRenderableWidget(Button.builder(Component.literal("Enabled: " + (active ? "ON" : "OFF")), button -> {
            active = !active;
            button.setMessage(Component.literal("Enabled: " + (active ? "ON" : "OFF")));
        }).bounds(x, top, 204, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Effect: " + friendly(selectedMode)), button -> {
            selectedMode = selectedMode.equals("outline") ? "recolor" : "outline";
            button.setMessage(Component.literal("Effect: " + friendly(selectedMode)));
        }).bounds(x + 208, top, 204, 20).build());

        // 17 independent toggles: any number of original shulker colors can be targeted.
        for (int i = 0; i < ShulkerHighlighter.COLORS.length; i++) {
            final String color = ShulkerHighlighter.COLORS[i];
            final int bx = x + (i % 4) * (buttonWidth + gap);
            final int by = top + 48 + (i / 4) * 24;
            addRenderableWidget(Button.builder(targetLabel(color), button -> {
                if (!selectedTargets.add(color)) selectedTargets.remove(color);
                button.setMessage(targetLabel(color));
            }).bounds(bx, by, buttonWidth, 20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Select ALL"), button -> {
            selectedTargets.addAll(Arrays.asList(ShulkerHighlighter.COLORS));
            rebuildWidgets();
        }).bounds(x + 102, top + 48 + 4 * 24, 98, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Select NONE"), button -> {
            selectedTargets.clear();
            rebuildWidgets();
        }).bounds(x + 204, top + 48 + 4 * 24, 98, 20).build());

        for (int i = 0; i < PALETTE.length; i++) {
            final int color = PALETTE[i];
            int bx = x + (i % 8) * 52;
            int by = top + 232 + (i / 8) * 24;
            addRenderableWidget(Button.builder(
                Component.literal((selectedRgb == color ? "✓ " : "") + "■")
                    .withStyle(style -> style.withColor(color)),
                button -> {
                    selectedRgb = color;
                    hexBox.setValue(hex(color));
                    rebuildWidgets();
                }
            ).bounds(bx, by, 48, 20).build());
        }

        hexBox = new EditBox(this.font, x + 132, top + 291, 148, 20, Component.literal("Custom RGB hex"));
        hexBox.setMaxLength(7);
        hexBox.setValue(hex(selectedRgb));
        hexBox.setResponder(input -> {
            int parsed = parseHex(input);
            if (saveButton != null) saveButton.active = parsed >= 0;
            hexBox.setTextColor(parsed >= 0 ? 0xFFE5E5E5 : 0xFFFF5555);
            if (parsed >= 0) selectedRgb = parsed;
        });
        addRenderableWidget(hexBox);

        saveButton = addRenderableWidget(Button.builder(Component.literal("Save & Done"), button -> saveAndExit())
            .bounds(x, top + 329, 204, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> minecraft.gui.setScreen(parent))
            .bounds(x + 208, top + 329, 204, 20).build());
    }

    private Component targetLabel(String color) {
        return Component.literal((selectedTargets.contains(color) ? "[x] " : "[ ] ") + friendly(color));
    }

    private static String friendly(String key) {
        StringBuilder result = new StringBuilder();
        for (String word : key.split("_")) {
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }
    private static String hex(int color) {
        return String.format(Locale.ROOT, "#%06X", color & 0xFFFFFF);
    }
    private static int parseHex(String text) {
        String value = text.startsWith("#") ? text.substring(1) : text;
        if (!value.matches("[0-9a-fA-F]{6}")) return -1;
        try { return Integer.parseInt(value, 16); }
        catch (NumberFormatException ex) { return -1; }
    }
    private void saveAndExit() {
        if (parseHex(hexBox.getValue()) < 0) return;
        ShulkerHighlighter.enabled = active;
        ShulkerHighlighter.targets.clear();
        ShulkerHighlighter.targets.addAll(selectedTargets);
        ShulkerHighlighter.mode = selectedMode;
        ShulkerHighlighter.rgb = selectedRgb;
        ShulkerHighlighter.save();
        minecraft.gui.setScreen(parent);
    }
    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
