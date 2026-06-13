package com.umg.multineokeybinds.core;

import net.minecraft.client.KeyMapping;

import java.util.HashMap;
import java.util.Map;

public class MultiKeyManager {

    private static final Map<KeyMapping, MultiKeyData> DATA = new HashMap<>();

    public static MultiKeyData get(KeyMapping key) {
        return DATA.computeIfAbsent(key, k -> new MultiKeyData());
    }

    public static Map<KeyMapping, MultiKeyData> all() {
        return DATA;
    }
}