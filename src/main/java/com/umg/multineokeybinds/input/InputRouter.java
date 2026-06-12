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

@EventBusSubscriber(
        modid = "multineokeybinds",
        value = Dist.CLIENT
)
public class InputRouter {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return;
        }

        long window = mc.getWindow().getWindow();

        for (KeyMapping mapping : MultiKeyRegistry.all()) {

            List<Integer> keys = MultiKeyRegistry.get(mapping);

            for (int key : keys) {

                boolean down = InputConstants.isKeyDown(window, key);

                if (down && !KeyStateTracker.wasDown(key)) {

                    KeyStateTracker.set(key, true);

                    // Simulate pressing the vanilla keybind
                    mapping.setDown(true);
                }

                if (!down) {

                    KeyStateTracker.set(key, false);

                    mapping.setDown(false);
                }
            }
        }
    }
}