package com.example.shulkerhighlight;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Real multi-page Mod Menu UI. Every selection is local until Save & Done. */
public final class ShulkerSettingsScreen extends Screen {
    private static final int[] PALETTE = {
        0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00,
        0x00AA00, 0x00FFFF, 0x007FFF, 0x0000FF,
        0x7F00FF, 0xFF00FF, 0xFF69B4, 0xFFFFFF,
        0xC0C0C0, 0x808080, 0x8B4513, 0x000000
    };
    private static final String[] PALETTE_NAMES = {
        "Red", "Orange", "Yellow", "Lime", "Green", "Cyan", "Sky", "Blue",
        "Purple", "Magenta", "Pink", "White", "Silver", "Gray", "Brown", "Black"
    };
    private static final int[] DYE_RGB = {
        0xF9FFFE, 0xF9801D, 0xC74EBD, 0x3AB3DA, 0xFED83D, 0x80C71F,
        0xF38BAA, 0x474F52, 0x9D9D97, 0x169C9C, 0x8932B8, 0x3C44AA,
        0x835432, 0x5E7C16, 0xB02E26, 0x1D1D21, 0xAAAAAA
    };
    private enum Page { HOME, TARGETS, COLOR }
    private final Screen parent;
    private Page page = Page.HOME;
    private boolean active;
    private String selectedMode;
    private final Set<String> selectedTargets = new LinkedHashSet<>();
    private int selectedRgb;
    private EditBox hexBox;
    private Button saveButton;

    public ShulkerSettingsScreen(Screen parent) {
        super(Component.literal("Shulker Highlighter"));
        this.parent = parent;
        active = ShulkerHighlighter.enabled;
        selectedMode = ShulkerHighlighter.mode;
        selectedTargets.addAll(ShulkerHighlighter.targets);
        selectedRgb = ShulkerHighlighter.rgb;
    }

    @Override
    protected void init() {
        // At Minecraft's default GUI scale the UI fits on even small screens.
        int width = Math.min(330, this.width - 24);
        int x = (this.width - width) / 2;
        int y = Math.max(12, (this.height - 242) / 2);
        switch (page) {
            case HOME -> home(x, y, width);
            case TARGETS -> targets(x, y, width);
            case COLOR -> colors(x, y, width);
        }
    }

    private void home(int x, int y, int width) {
        button("Enabled: " + (active ? "ON" : "OFF"), x, y, width, () -> {
            active = !active;
            rebuildWidgets();
        });
        button("Effect: " + (selectedMode.equals("outline") ? "Outline" : "Recolor"), x, y + 29, width, () -> {
            selectedMode = selectedMode.equals("outline") ? "recolor" : "outline";
            rebuildWidgets();
        });
        button("Shulker colors: " + selectedTargets.size() + " selected  >", x, y + 58, width,
            () -> open(Page.TARGETS));
        button("Highlight color: " + hex(selectedRgb) + "  >", x, y + 87, width,
            () -> open(Page.COLOR));
        button("Save & Done", x, y + 145, width / 2 - 3, this::saveAndExit);
        button("Cancel", x + width / 2 + 3, y + 145, width / 2 - 3, this::goBack);
    }

    private void targets(int x, int y, int width) {
        int gap = 3;
        int cellWidth = (width - gap * 3) / 4;
        for (int i = 0; i < ShulkerHighlighter.COLORS.length; i++) {
            final String target = ShulkerHighlighter.COLORS[i];
            final int index = i;
            int bx = x + (i % 4) * (cellWidth + gap);
            int by = y + (i / 4) * 27;
            int tint = DYE_RGB[i];
            Component caption = Component.literal((selectedTargets.contains(target) ? "[x] " : "[ ] ") + friendly(target))
                .withStyle(style -> style.withColor(tint));
            addRenderableWidget(Button.builder(caption, b -> {
                if (!selectedTargets.add(target)) selectedTargets.remove(target);
                b.setMessage(Component.literal((selectedTargets.contains(target) ? "[x] " : "[ ] ") + friendly(target))
                    .withStyle(style -> style.withColor(DYE_RGB[index])));
            }).bounds(bx, by, cellWidth, 20).build());
        }
        button("Select all", x, y + 146, width / 2 - 3, () -> {
            selectedTargets.addAll(Arrays.asList(ShulkerHighlighter.COLORS)); rebuildWidgets();
        });
        button("Clear", x + width / 2 + 3, y + 146, width / 2 - 3, () -> {
            selectedTargets.clear(); rebuildWidgets();
        });
        button("Back to settings", x, y + 180, width, () -> open(Page.HOME));
    }

    private void colors(int x, int y, int width) {
        int gap = 3;
        int cellWidth = (width - gap * 3) / 4;
        for (int i = 0; i < PALETTE.length; i++) {
            final int rgb = PALETTE[i];
            int bx = x + (i % 4) * (cellWidth + gap);
            int by = y + (i / 4) * 28;
            String name = PALETTE_NAMES[i];
            // Text is tinted with the actual RGB color, instead of a generic black/gray button label.
            Component caption = Component.literal((selectedRgb == rgb ? "[x] " : "[ ] ") + "■ " + name)
                .withStyle(style -> style.withColor(rgb));
            addRenderableWidget(Button.builder(caption, b -> {
                selectedRgb = rgb;
                rebuildWidgets();
            }).bounds(bx, by, cellWidth, 22).build());
        }
        hexBox = new EditBox(this.font, x + 95, y + 128, width - 95, 20, Component.literal("Custom RGB hex"));
        hexBox.setMaxLength(7);
        hexBox.setValue(hex(selectedRgb));
        hexBox.setResponder(text -> {
            int parsed = parseHex(text);
            if (parsed >= 0) selectedRgb = parsed;
            hexBox.setTextColor(parsed >= 0 ? 0xFFE5E5E5 : 0xFFFF5555);
            if (saveButton != null) saveButton.active = parsed >= 0;
        });
        addRenderableWidget(hexBox);
        saveButton = addRenderableWidget(Button.builder(Component.literal("Use this color"), b -> {
            int parsed = parseHex(hexBox.getValue());
            if (parsed >= 0) { selectedRgb = parsed; open(Page.HOME); }
        }).bounds(x, y + 163, width, 20).build());
    }

    private void button(String label, int x, int y, int width, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run()).bounds(x, y, width, 20).build());
    }
    private void open(Page next) { this.page = next; rebuildWidgets(); }
    private void goBack() { minecraft.gui.setScreen(parent); }
    private static String friendly(String name) {
        StringBuilder out = new StringBuilder();
        for (String part : name.split("_")) {
            if (!out.isEmpty()) out.append(' ');
            out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return out.toString();
    }
    private static String hex(int rgb) { return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF); }
    private static int parseHex(String raw) {
        String s = raw.startsWith("#") ? raw.substring(1) : raw;
        if (!s.matches("[0-9a-fA-F]{6}")) return -1;
        try { return Integer.parseInt(s, 16); }
        catch (NumberFormatException e) { return -1; }
    }
    private void saveAndExit() {
        ShulkerHighlighter.enabled = active;
        ShulkerHighlighter.targets.clear();
        ShulkerHighlighter.targets.addAll(selectedTargets);
        ShulkerHighlighter.mode = selectedMode;
        ShulkerHighlighter.rgb = selectedRgb;
        ShulkerHighlighter.save();
        goBack();
    }
    @Override
    public void onClose() {
        if (page != Page.HOME) open(Page.HOME);
        else goBack();
    }
}
