package com.umg.multineokeybinds.gui;

import com.umg.multineokeybinds.client.ControlsExtension;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
public class KeyEntryMixin {

    @Shadow @Final private KeyMapping key;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(GuiGraphics gg, int idx, int top, int left,
                        int width, int height, int mouseX, int mouseY,
                        boolean hovering, float pt, CallbackInfo ci) {

        int x = left + width - 40;

        Button add = Button.builder(Component.literal("+"), b -> {
            KeyListener.start(this.key);
        }).bounds(x, top, 20, 20).build();

        add.render(gg, mouseX, mouseY, pt);
    }
}