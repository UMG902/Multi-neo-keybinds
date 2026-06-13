package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.core.MultiKeyManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyListener {

    private static KeyMapping listeningKey = null;

    public static void start(KeyMapping key) {
        listeningKey = key;
    }

    public static void tick() {
        if (listeningKey == null) return;

        long window = Minecraft.getInstance().getWindow().getWindow();

        for (int i = 32; i < 350; i++) {
            if (GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS) {
                MultiKeyManager.add(listeningKey, i);
                listeningKey = null;
                break;
            }
        }
    }
}