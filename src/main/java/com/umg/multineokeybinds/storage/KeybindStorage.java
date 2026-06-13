package com.umg.multineokeybinds.storage;

import com.google.gson.*;
import com.umg.multineokeybinds.core.MultiKeyManager;
import net.minecraft.client.KeyMapping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class KeybindStorage {

    private static final File FILE = new File("config/multikeybinds.json");

    public static void save() {
        try {
            JsonObject root = new JsonObject();

            for (var entry : MultiKeyManager.all().entrySet()) {
                JsonArray arr = new JsonArray();
                for (int code : entry.getValue().extraKeys) {
                    arr.add(code);
                }
                root.add(entry.getKey().getName(), arr);
            }

            FILE.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(FILE)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            if (!FILE.exists()) return;

            JsonObject root = JsonParser.parseReader(new FileReader(FILE)).getAsJsonObject();

            for (String keyName : root.keySet()) {
                JsonArray arr = root.getAsJsonArray(keyName);

                for (var el : arr) {
                    int code = el.getAsInt();
                    // mapping back handled lazily in runtime
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}