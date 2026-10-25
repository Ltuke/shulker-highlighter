package com.example.shulkerhighlight;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.DyeColor;

/** Client-only settings. The server and the real block state are never changed. */
public final class ShulkerHighlighter implements ClientModInitializer {
    public static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black", "undyed"
    };
    public static volatile boolean enabled = true;
    public static final Set<String> targets = Collections.synchronizedSet(new LinkedHashSet<>(Arrays.asList("green")));
    public static volatile String mode = "outline";
    public static volatile int rgb = 0xFF0000;
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("shulker-highlighter.properties");

    @Override
    public void onInitializeClient() {
        load();
    }

    public static boolean matches(DyeColor dye) {
        return enabled && targets.contains(dye == null ? "undyed" : dye.name().toLowerCase(Locale.ROOT));
    }

    public static void load() {
        if (!Files.exists(CONFIG)) return;
        try (InputStream stream = Files.newInputStream(CONFIG)) {
            Properties p = new Properties();
            p.load(stream);
            enabled = Boolean.parseBoolean(p.getProperty("enabled", "true"));
            String raw = p.getProperty("targets", p.getProperty("target", "green"));
            Set<String> valid = new LinkedHashSet<>();
            for (String choice : raw.split(",")) {
                for (String allowed : COLORS) if (allowed.equals(choice.trim())) valid.add(allowed);
            }
            targets.clear();
            targets.addAll(valid);
            String m = p.getProperty("mode", "outline");
            if ("outline".equals(m) || "recolor".equals(m)) mode = m;
            rgb = Integer.parseInt(p.getProperty("rgb", "FF0000"), 16) & 0xFFFFFF;
        } catch (Exception e) {
            System.err.println("[Shulker Highlighter] Failed to load config: " + e);
        }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            p.setProperty("enabled", Boolean.toString(enabled));
            p.setProperty("targets", String.join(",", targets));
            p.setProperty("mode", mode);
            p.setProperty("rgb", String.format(Locale.ROOT, "%06X", rgb));
            Files.createDirectories(CONFIG.getParent());
            try (OutputStream stream = Files.newOutputStream(CONFIG)) {
                p.store(stream, "Shulker Highlighter client settings");
            }
        } catch (Exception e) {
            System.err.println("[Shulker Highlighter] Failed to save config: " + e);
        }
    }
}
