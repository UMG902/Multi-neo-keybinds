package com.umg.multineokeybinds.client;

public interface KeyEntryExtension {
    boolean multikeybinds$handleClick(double mouseX, double mouseY, int button);
    boolean multikeybinds$handleRelease(double mouseX, double mouseY, int button);
}