package com.umg.multineokeybinds.mixin;

import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsList.class)
public abstract class KeyBindsListMixin {

    @Shadow protected abstract int getItemHeight();

    @Inject(method = "getItemHeight", at = @At("HEAD"), cancellable = true)
    private void multikeybinds$adjustItemHeight(CallbackInfoReturnable<Integer> cir) {
        // This will be overridden per-entry, but we need a base height
        cir.setReturnValue(20);
    }
}