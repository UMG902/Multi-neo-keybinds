package com.umg.multineokeybinds.input;

import com.umg.multineokeybinds.core.MultiKeyManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class MultiKeyInput {

    public static boolean isDown(KeyMapping key) {

        if (key.isDown()) return true;

        long window = Minecraft.getInstance().getWindow().getWindow();

        for (int code : MultiKeyManager.get(key).extraKeys) {
            if (GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS) {
                return true;
            }
        }

        return false;
    }
}