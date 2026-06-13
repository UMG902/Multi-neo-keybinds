package com.umg.multineokeybinds.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(KeyBindsList.class)
public interface IKeyBindsList {
    @Accessor("children")
    List<KeyBindsList.Entry> getChildren();

    @Accessor("children")
    void setChildren(List<KeyBindsList.Entry> children);

    @Accessor("keyBindsScreen")
    KeyBindsScreen getKeyBindsScreen();

    @Accessor("getScrollbarPosition")
    int getScrollbarPosition();

    // Custom methods for our logic
    void startAddingExtra(KeyMapping mapping);
    void startEditingExtra(KeyMapping mapping, int oldKey);
}