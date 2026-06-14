package com.umg.multineokeybinds.core;

import java.util.HashMap;
import java.util.Map;

public class KeyStateTracker {

    private static final Map<Integer, Boolean> STATE = new HashMap<>();

    public static boolean wasDown(int key) {
        return STATE.getOrDefault(key, false);
    }

    public static void set(int key, boolean down) {
        STATE.put(key, down);
    }
}