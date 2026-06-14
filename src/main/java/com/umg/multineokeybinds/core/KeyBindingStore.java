package com.umg.multineokeybinds.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingStore {
    private static final File CONFIG_FILE = new File("config/multineokeybinds.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        System.out.println("[MultiNeoKeybinds] LOAD CALLED! File exists: " + CONFIG_FILE.exists());
        if (!CONFIG_FILE.exists()) return;

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<Map<String, List<Integer>>>(){}.getType();
            Map<String, List<Integer>> data = GSON.fromJson(reader, type);
            if (data == null) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.options == null) return;

            for (KeyMapping mapping : mc.options.keyMappings) {
                // Use saveString() to get the unique ID (e.g., "key.jump")
                String id = mapping.saveString();
                if (data.containsKey(id)) {
                    System.out.println("[MultiNeoKeybinds] Loading " + id + " with keys: " + data.get(id));
                    for (int key : data.get(id)) {
                        MultiKeyRegistry.add(mapping, key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[MultiNeoKeybinds] Failed to load config!");
            e.printStackTrace();
        }
    }

    public static void save() {
        System.out.println("[MultiNeoKeybinds] SAVE CALLED! Registry size: " + MultiKeyRegistry.raw().size());
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            Map<String, List<Integer>> data = new HashMap<>();

            for (Map.Entry<KeyMapping, List<Integer>> entry : MultiKeyRegistry.raw().entrySet()) {
                // Use saveString() to get the unique ID (e.g., "key.jump")
                String id = entry.getKey().saveString();
                System.out.println("[MultiNeoKeybinds] Saving " + id + " with keys: " + entry.getValue());
                data.put(id, entry.getValue());
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
            System.out.println("[MultiNeoKeybinds] Successfully saved to " + CONFIG_FILE.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[MultiNeoKeybinds] Failed to save config!");
            e.printStackTrace();
        }
    }
}