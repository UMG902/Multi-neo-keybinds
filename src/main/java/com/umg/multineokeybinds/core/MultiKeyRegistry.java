package com.umg.multineokeybinds.core;

import net.minecraft.client.KeyMapping;

import java.util.*;

public class MultiKeyRegistry {

    private static final Map<KeyMapping, List<Integer>> MAP = new HashMap<>();

    // ==================================================
    // GET ALL KEYS FOR A KEYBIND
    // ==================================================
    public static List<Integer> get(KeyMapping mapping) {
        return MAP.getOrDefault(mapping, new ArrayList<>());
    }

    // ==================================================
    // ADD A KEY TO A KEYBIND
    // ==================================================
    public static void add(KeyMapping mapping, int key) {
        MAP.computeIfAbsent(mapping, k -> new ArrayList<>());

        List<Integer> list = MAP.get(mapping);

        if (!list.contains(key)) {
            list.add(key);
        }
    }

    // ==================================================
    // REMOVE A KEY FROM A KEYBIND
    // ==================================================
    public static void remove(KeyMapping mapping, int key) {
        List<Integer> list = MAP.get(mapping);
        if (list == null) return;

        list.remove(Integer.valueOf(key));

        if (list.isEmpty()) {
            MAP.remove(mapping);
        }
    }

    // ==================================================
    // CLEAR ALL (optional reset button support)
    // ==================================================
    public static void clear(KeyMapping mapping) {
        MAP.remove(mapping);
    }

    // ==================================================
    // CLEAR EVERYTHING
    // ==================================================
    public static void clearAll() {
        MAP.clear();
    }

    public static Set<KeyMapping> all() {
        return MAP.keySet();
    }

    // ==================================================
    // OPTIONAL: expose map (debug / save system later)
    // ==================================================
    public static Map<KeyMapping, List<Integer>> raw() {
        return MAP;
    }
}