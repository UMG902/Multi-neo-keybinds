package com.umg.multineokeybinds.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.core.KeyStateTracker;
import com.umg.multineokeybinds.core.MultiKeyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.List;

@EventBusSubscriber(modid = "multineokeybinds", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class InputRouter {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getWindow() == null) return;

        long window = mc.getWindow().getWindow();

        for (KeyMapping mapping : MultiKeyRegistry.all()) {
            List<Integer> keys = MultiKeyRegistry.get(mapping);
            boolean anyDown = false;

            for (int key : keys) {
                boolean down = InputConstants.isKeyDown(window, key);

                if (down) {
                    anyDown = true;
                    if (!KeyStateTracker.wasDown(key)) {
                        KeyStateTracker.set(key, true);
                        // click() is required for one-off actions like jumping or using items
                        mapping.click(InputConstants.Type.KEYSYM.getOrCreate(key));
                    }
                } else {
                    if (KeyStateTracker.wasDown(key)) {
                        KeyStateTracker.set(key, false);
                        // We intentionally do NOT call mapping.release() here.
                        // In 1.21, release() is private and only sets isDown = false.
                        // If we called it, we would overwrite the vanilla key's state if it's still held!
                    }
                }
            }

            // Check if the original vanilla bound key is currently physically held down
            boolean vanillaKeyDown = InputConstants.isKeyDown(window, mapping.getKey().getValue());

            // Force the continuous state to true if ANY custom key is held,
            // OR if the original vanilla key is currently held.
            mapping.setDown(anyDown || vanillaKeyDown);
        }
    }
}