package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.core.KeyBindingManager;
import com.umg.multineokeybinds.core.KeyBindingStore;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "multineokeybinds", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientInit {
    private static boolean initialized = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // Wait until Minecraft and its options are fully loaded
        if (!initialized && Minecraft.getInstance().options != null) {
            initialized = true;
            KeyBindingStore.load();
            KeyBindingManager.init();
        }
    }
}