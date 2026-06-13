package com.umg.multineokeybinds.core;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MultiKeyRegistry {
    private static final Map<KeyMapping, List<Integer>> REGISTRY = Collections.synchronizedMap(new IdentityHashMap<>());

    private MultiKeyRegistry() {
    }

    public static List<Integer> get(KeyMapping mapping) {
        synchronized (REGISTRY) {
            List<Integer> values = REGISTRY.get(mapping);
            return values == null ? List.of() : List.copyOf(values);
        }
    }

    public static Map<KeyMapping, List<Integer>> raw() {
        synchronized (REGISTRY) {
            Map<KeyMapping, List<Integer>> copy = new LinkedHashMap<>();
            for (Map.Entry<KeyMapping, List<Integer>> entry : REGISTRY.entrySet()) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return copy;
        }
    }

    public static void set(KeyMapping mapping, List<Integer> keys) {
        synchronized (REGISTRY) {
            ArrayList<Integer> list = new ArrayList<>();
            for (Integer key : keys) {
                if (key != null && !list.contains(key)) {
                    list.add(key);
                }
            }
            REGISTRY.put(mapping, list);
        }
    }

    public static void add(KeyMapping mapping, int keyCode) {
        synchronized (REGISTRY) {
            List<Integer> list = REGISTRY.computeIfAbsent(mapping, k -> new ArrayList<>());
            if (!list.contains(keyCode)) {
                list.add(keyCode);
            }
        }
    }

    public static void remove(KeyMapping mapping, int keyCode) {
        synchronized (REGISTRY) {
            List<Integer> list = REGISTRY.get(mapping);
            if (list == null) {
                return;
            }
            list.remove(Integer.valueOf(keyCode));
            if (list.isEmpty()) {
                REGISTRY.remove(mapping);
            }
        }
    }

    public static Iterable<KeyMapping> all() {
        synchronized (REGISTRY) {
            return List.copyOf(REGISTRY.keySet());
        }
    }

    public static void clear() {
        synchronized (REGISTRY) {
            REGISTRY.clear();
        }
    }
}
