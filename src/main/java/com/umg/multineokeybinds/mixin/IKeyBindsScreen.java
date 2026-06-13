package com.umg.multineokeybinds.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsScreen.class)
public interface IKeyBindsScreen {
    @Accessor("selectedKey")
    KeyMapping getSelectedKey();

    @Accessor("selectedKey")
    void setSelectedKey(KeyMapping key);
}