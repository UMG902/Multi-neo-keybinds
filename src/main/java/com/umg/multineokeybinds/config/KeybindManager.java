package com.umg.multineokeybinds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class KeybindManager {
    private static final File CONFIG_FILE = new File(Minecraft.getInstance().gameDirectory, "config/multi-neo-keybinds.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, java.util.List<Integer>> EXTRA_BINDINGS = new HashMap<>();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<String, java.util.List<Integer>>>(){}.getType();
                Map<String, java.util.List<Integer>> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    EXTRA_BINDINGS.putAll(loaded);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(EXTRA_BINDINGS, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<Integer> getExtraKeys(String keybindId) {
        return EXTRA_BINDINGS.getOrDefault(keybindId, new java.util.ArrayList<>());
    }

    public static void addExtraKey(String keybindId, int keyCode) {
        EXTRA_BINDINGS.computeIfAbsent(keybindId, k -> new java.util.ArrayList<>()).add(keyCode);
        save();
    }

    public static void removeExtraKey(String keybindId, int keyCode) {
        if (EXTRA_BINDINGS.containsKey(keybindId)) {
            EXTRA_BINDINGS.get(keybindId).remove(Integer.valueOf(keyCode));
            if (EXTRA_BINDINGS.get(keybindId).isEmpty()) {
                EXTRA_BINDINGS.remove(keybindId);
            }
            save();
        }
    }

    public static void updateExtraKey(String keybindId, int oldKeyCode, int newKeyCode) {
        removeExtraKey(keybindId, oldKeyCode);
        addExtraKey(keybindId, newKeyCode);
    }

    public static boolean hasExtraKeys(KeyMapping mapping) {
        return EXTRA_BINDINGS.containsKey(mapping.getName());
    }

    public static boolean isExtraKeyDown(KeyMapping mapping) {
        String id = mapping.getName();
        if (!EXTRA_BINDINGS.containsKey(id)) return false;

        long window = Minecraft.getInstance().getWindow().getWindow();
        for (int code : EXTRA_BINDINGS.get(id)) {
            int state = GLFW.glfwGetKey(window, code);
            if (state == GLFW.GLFW_PRESS) {
                return true;
            }
        }
        return false;
    }

    public static void handleKeyPress(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());

        for (KeyMapping mapping : mc.options.keyMappings) {
            String id = mapping.getName();
            if (EXTRA_BINDINGS.containsKey(id) && EXTRA_BINDINGS.get(id).contains(event.getKey())) {
                KeyMapping.click(key);
            }
        }
    }
}