package com.umg.multineokeybinds.mixin;

import com.umg.multineokeybinds.ProxyKeyManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin {

    // 1. Give vanilla our custom, perfectly ordered array.
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;"))
    private KeyMapping[] customKeyMappings(Options options) {
        KeyMapping[] original = options.keyMappings.clone();
        Arrays.sort(original); // Sort original vanilla style first

        List<KeyMapping> finalList = new ArrayList<>();
        for (KeyMapping key : original) {
            // Completely hide all proxy keys from the main layout pass
            if (ProxyKeyManager.isProxy(key)) continue;

            finalList.add(key); // Add the vanilla/modded target key

            // Add any linked proxies immediately after it
            finalList.addAll(ProxyKeyManager.getProxiesFor(key));
        }

        return finalList.toArray(new KeyMapping[0]);
    }

    // 2. Prevent vanilla from messing up the perfect order we just established.
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;sort([Ljava/lang/Object;)V"))
    private void preventVanillaSort(Object[] array) {
        // Do nothing!
    }

    // 3. Dynamically rename the proxies to "Target Name Extra X" just before they are rendered
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent renameProxiesDynamically(String translationKey) {
        KeyMapping mapping = ProxyKeyManager.getMappingByName(translationKey);

        if (mapping != null && ProxyKeyManager.isProxy(mapping)) {
            KeyMapping target = ProxyKeyManager.getTargetFor(mapping);
            if (target != null) {
                List<KeyMapping> siblingProxies = ProxyKeyManager.getProxiesFor(target);
                int index = siblingProxies.indexOf(mapping) + 1; // 1-based index

                // Returns e.g., "Jump Extra 1"
                return Component.translatable(target.getName()).append(Component.literal(" Extra " + index));
            }
        }
        return Component.translatable(translationKey);
    }
}