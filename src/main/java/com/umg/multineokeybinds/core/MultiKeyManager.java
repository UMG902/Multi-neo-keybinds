package com.umg.multineokeybinds.core;

import net.minecraft.client.KeyMapping;

import java.util.*;

public class MultiKeyManager {

    private static final Map<KeyMapping, List<Integer>> DATA = new HashMap<>();

    public static List<Integer> get(KeyMapping key) {
        return DATA.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public static void add(KeyMapping key, int code) {
        get(key).add(code);
    }

    public static void remove(KeyMapping key, int code) {
        get(key).remove((Integer) code);
    }
}