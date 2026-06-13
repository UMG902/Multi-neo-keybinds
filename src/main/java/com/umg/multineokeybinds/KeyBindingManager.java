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

public final class KeyBindingStore {
    private static final File CONFIG_FILE = new File("config/multineokeybinds.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private KeyBindingStore() {
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<Map<String, List<Integer>>>() {}.getType();
            Map<String, List<Integer>> data = GSON.fromJson(reader, type);
            if (data == null || data.isEmpty()) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.options == null) {
                return;
            }

            MultiKeyRegistry.clear();

            for (KeyMapping mapping : mc.options.keyMappings) {
                List<Integer> keys = data.get(mapping.saveString());
                if (keys != null && !keys.isEmpty()) {
                    MultiKeyRegistry.set(mapping, keys);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            Map<String, List<Integer>> data = new HashMap<>();
            for (Map.Entry<KeyMapping, List<Integer>> entry : MultiKeyRegistry.raw().entrySet()) {
                data.put(entry.getKey().saveString(), entry.getValue());
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
