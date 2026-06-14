package com.umg.multineokeybinds.core;

public class KeyCodec {

    public static boolean isPressed(long window, int key) {
        return org.lwjgl.glfw.GLFW.glfwGetKey(window, key) == 1;
    }
}