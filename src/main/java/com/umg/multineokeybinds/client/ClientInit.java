package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.core.KeyBindingManager;
import com.umg.multineokeybinds.core.KeyBindingStore;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "multineokeybinds", value = Dist.CLIENT)
public final class ClientInit {
    private static boolean initialized = false;

    private ClientInit() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (initialized) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) {
            return;
        }

        initialized = true;
        KeyBindingStore.load();
        KeyBindingManager.init();
    }
}
