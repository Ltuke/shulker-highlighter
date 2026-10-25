package com.example.shulkerhighlight;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Opened using Mod Menu -> Shulker Highlighter -> Configure. */
public final class ShulkerSettingsScreen extends Screen {
    private static final int[] PRESETS = {
        0xFF0000, 0xFFAA00, 0xFFFF00, 0x00FF00, 0x00FFFF,
        0x0000FF, 0xFF00FF, 0xFFFFFF
    };
    private final Screen parent;
    private boolean active;
    private String selectedColor;
    private String selectedMode;
    private int selectedRgb;
    private int presetIndex;
    private EditBox hexBox;
    private Button saveButton;

    public ShulkerSettingsScreen(Screen parent) {
        super(Component.literal("Shulker Highlighter"));
        this.parent = parent;
        this.active = ShulkerHighlighter.enabled;
        this.selectedColor = ShulkerHighlighter.target;
        this.selectedMode = ShulkerHighlighter.mode;
        this.selectedRgb = ShulkerHighlighter.rgb;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 115;
        int y = Math.max(28, this.height / 2 - 106);

        addRenderableWidget(Button.builder(Component.literal("Enabled: " + (active ? "ON" : "OFF")), button -> {
            active = !active;
            button.setMessage(Component.literal("Enabled: " + (active ? "ON" : "OFF")));
        }).bounds(x, y, 230, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Original shulker: " + friendly(selectedColor)), button -> {
            int i = Arrays.asList(ShulkerHighlighter.COLORS).indexOf(selectedColor);
            selectedColor = ShulkerHighlighter.COLORS[(i + 1) % ShulkerHighlighter.COLORS.length];
            button.setMessage(Component.literal("Original shulker: " + friendly(selectedColor)));
        }).bounds(x, y + 25, 230, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Effect: " + friendly(selectedMode)), button -> {
            selectedMode = selectedMode.equals("outline") ? "recolor" : "outline";
            button.setMessage(Component.literal("Effect: " + friendly(selectedMode)));
        }).bounds(x, y + 50, 230, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Next preset color"), button -> {
            selectedRgb = PRESETS[presetIndex++ % PRESETS.length];
            hexBox.setValue(hex(selectedRgb));
        }).bounds(x, y + 75, 230, 20).build());

        hexBox = new EditBox(this.font, x + 85, y + 102, 145, 20, Component.literal("RGB hex color"));
        hexBox.setMaxLength(7);
        hexBox.setValue(hex(selectedRgb));
        hexBox.setResponder(input -> {
            int parsed = parseHex(input);
            saveButton.active = parsed >= 0;
            hexBox.setTextColor(parsed >= 0 ? 0xFFE5E5E5 : 0xFFFF5555);
            if (parsed >= 0) selectedRgb = parsed;
        });
        addRenderableWidget(hexBox);

        saveButton = addRenderableWidget(Button.builder(Component.literal("Save & Done"), button -> saveAndExit())
            .bounds(x, y + 132, 112, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> minecraft.setScreen(parent))
            .bounds(x + 118, y + 132, 112, 20).build());
    }

    private static String friendly(String key) {
        String[] words = key.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
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
        ShulkerHighlighter.target = selectedColor;
        ShulkerHighlighter.mode = selectedMode;
        ShulkerHighlighter.rgb = selectedRgb;
        ShulkerHighlighter.save();
        minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent); // Escape cancels unapplied changes
    }
}
