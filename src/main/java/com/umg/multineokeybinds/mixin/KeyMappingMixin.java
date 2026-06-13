package com.umg.multineokeybinds.mixin;

import com.umg.multineokeybinds.input.MultiKeyInput;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Redirect(
            method = "isDown",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyMapping;isDown:Z")
    )
    private static boolean hook(KeyMapping key) {
        return MultiKeyInput.isDown(key);
    }
}