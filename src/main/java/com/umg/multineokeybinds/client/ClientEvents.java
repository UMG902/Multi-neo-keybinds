package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.client.KeyListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber
public class ClientEvents {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            KeyListener.tick();
        }
    }
}