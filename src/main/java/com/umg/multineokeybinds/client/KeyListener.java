package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.core.MultiKeyManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyListener {

    private static KeyMapping listening;

    public static void start(KeyMapping key) {
        listening = key;
    }

    public static void tick() {
        if (listening == null) return;

        long window = Minecraft.getInstance().getWindow().getWindow();

        for (int i = 32; i < 350; i++) {
            if (GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS) {
                MultiKeyManager.get(listening).extraKeys.add(i);
                listening = null;
                break;
            }
        }
    }
}