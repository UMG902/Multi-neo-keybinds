package com.umg.multineokeybinds.mixin; // Adjust package as needed

import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen; // Import Screen
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsSubScreen.class)
public interface OptionsSubScreenAccessor {
    // Keep your existing accessor
    @Accessor("list")
    OptionsList getList();

    // Add this new accessor
    @Accessor("lastScreen")
    Screen getLastScreen();
}