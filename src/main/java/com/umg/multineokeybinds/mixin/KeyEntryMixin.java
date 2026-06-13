package com.umg.multineokeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.client.ControlsExtension;
import com.umg.multineokeybinds.core.MultiKeyManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.ControlsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsList.KeyEntry.class)
public class KeyEntryMixin {

    @Shadow @Final private KeyMapping key;

    @Inject(method = "render", at = @At("TAIL"))
    private void multikeybinds$render(
            GuiGraphics gg,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean hovering,
            float partialTick,
            CallbackInfo ci
    ) {

        int addX = left + width - 40;

        Button addButton = Button.builder(Component.literal("+"), b -> {
            if (Minecraft.getInstance().screen instanceof ControlsExtension ext) {
                ext.multineokeybinds$startListening(this.key);
            }
        }).bounds(addX, top, 20, 20).build();

        addButton.render(gg, mouseX, mouseY, partialTick);
    }
}