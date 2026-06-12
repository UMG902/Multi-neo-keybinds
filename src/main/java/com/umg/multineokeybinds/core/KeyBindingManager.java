package com.umg.multineokeybinds.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindingManager {

    public static void init() {

        Minecraft mc = Minecraft.getInstance();

        // USE ITEM (example global multi-key bind)
        MultiKeyRegistry.add(mc.options.keyUse, GLFW.GLFW_KEY_R);
        MultiKeyRegistry.add(mc.options.keyUse, GLFW.GLFW_KEY_KP_ADD);

        // JUMP example
        MultiKeyRegistry.add(mc.options.keyJump, GLFW.GLFW_KEY_V);
    }
}