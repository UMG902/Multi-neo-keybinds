package com.umg.multineokeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.client.KeyBindsScreenExtension;
import com.umg.multineokeybinds.core.MultiKeyRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsScreen.class)
public class KeyBindsScreenMixin implements KeyBindsScreenExtension {

    @Unique
    private KeyMapping multikeybinds$current = null;

    @Unique
    private boolean multikeybinds$listening = false;

    @Override
    public void multikeybinds$startListening(KeyMapping mapping) {
        this.multikeybinds$current = mapping;
        this.multikeybinds$listening = true;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void multikeybinds$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.multikeybinds$listening) {
            return;
        }

        if (keyCode == 256) {
            this.multikeybinds$listening = false;
            this.multikeybinds$current = null;
            cir.setReturnValue(true);
            return;
        }

        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (this.multikeybinds$current != null) {
            MultiKeyRegistry.add(this.multikeybinds$current, key.getValue());
            Minecraft.getInstance().options.save();
        }

        this.multikeybinds$listening = false;
        this.multikeybinds$current = null;
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void multikeybinds$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!this.multikeybinds$listening) {
            return;
        }

        if (this.multikeybinds$current != null) {
            InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
            MultiKeyRegistry.add(this.multikeybinds$current, key.getValue());
            Minecraft.getInstance().options.save();
        }

        this.multikeybinds$listening = false;
        this.multikeybinds$current = null;
        cir.setReturnValue(true);
    }
}