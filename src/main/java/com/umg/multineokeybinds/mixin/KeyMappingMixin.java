package com.umg.multineokeybinds.mixin;

import com.umg.multineokeybinds.config.KeybindManager;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
        KeyMapping self = (KeyMapping) (Object) this;
        // If the default key isn't down, but an extra key is, return true
        if (KeybindManager.isExtraKeyDown(self)) {
            cir.setReturnValue(true);
        }
    }

    // Ensure setDown is also respected or handled if necessary,
    // but primarily isDown controls the game loop for held actions.
}