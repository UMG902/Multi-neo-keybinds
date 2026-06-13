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
        if (!CONFIG_FILE.exists()) return;
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<Map<String, List<Integer>>>(){}.getType();
            Map<String, List<Integer>> data = GSON.fromJson(reader, type);

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.options == null) return;

            // Match the saved translation keys (e.g., "key.jump") to the actual KeyMapping objects
            for (KeyMapping mapping : mc.options.keyMappings) {
                String name = mapping.getName();
                if (data.containsKey(name)) {
                    for (int key : data.get(name)) {
                        MultiKeyRegistry.add(mapping, key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            Map<String, List<Integer>> data = new HashMap<>();

            // Save using the KeyMapping's translation name as the unique ID
            for (Map.Entry<KeyMapping, List<Integer>> entry : MultiKeyRegistry.raw().entrySet()) {
                data.put(entry.getKey().getName(), entry.getValue());
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}