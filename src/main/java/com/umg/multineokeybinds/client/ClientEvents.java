package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.client.KeyListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.ClientTickEvent;

@Mod.EventBusSubscriber
public class ClientEvents {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        KeyListener.tick();
    }
}