package com.umg.multineokeybinds;

import com.umg.multineokeybinds.config.KeybindManager;
import com.umg.multineokeybinds.mixin.IKeyBindsList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ExtraKeyEntry extends KeyBindsList.Entry {
    private final KeyMapping parentMapping;
    private final KeyBindsScreen screen;
    private final KeyBindsList list;
    private int keyCode;

    private final Button keyButton;
    private final Button removeButton;
    private final Button resetButton;

    public ExtraKeyEntry(KeyBindsScreen screen, KeyMapping parentMapping, int keyCode, KeyBindsList list) {
        this.screen = screen;
        this.parentMapping = parentMapping;
        this.list = list;
        this.keyCode = keyCode;

        this.removeButton = Button.builder(Component.literal("x"), btn -> {
            KeybindManager.removeExtraKey(parentMapping.getName(), this.keyCode);
            Minecraft.getInstance().setScreen(this.screen);
        }).width(20).build();

        this.keyButton = Button.builder(getKeyName(), btn -> {
            ((IKeyBindsList)list).startEditingExtra(parentMapping, this.keyCode);
        }).width(75).build();

        this.resetButton = Button.builder(Component.translatable("controls.reset"), btn -> {
            KeybindManager.removeExtraKey(parentMapping.getName(), this.keyCode);
            Minecraft.getInstance().setScreen(this.screen);
        }).width(50).build();
    }

    private Component getKeyName() {
        if (this.keyCode == 0) return Component.translatable("key.keyboard.unknown");
        String keyName = GLFW.glfwGetKeyName(this.keyCode, 0);
        return Component.translatable(keyName != null ? keyName : "Unknown");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        int scrollX = ((IKeyBindsList)list).getScrollbarPosition();

        int x = scrollX - this.resetButton.getWidth() - 10;

        this.resetButton.setPosition(x, top - 2);
        this.resetButton.render(guiGraphics, mouseX, mouseY, partialTick);

        x -= 5 + this.keyButton.getWidth();
        this.keyButton.setPosition(x, top - 2);
        this.keyButton.setMessage(getKeyName());
        this.keyButton.render(guiGraphics, mouseX, mouseY, partialTick);

        this.removeButton.setPosition(left + 20, top - 2);
        this.removeButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(removeButton, keyButton, resetButton);
    }

    public List<? extends NarratableEntry> narratables() {
        return List.of(removeButton, keyButton, resetButton);
    }

    @Override
    public void refreshEntry() {
        this.keyButton.setMessage(getKeyName());
    }
}