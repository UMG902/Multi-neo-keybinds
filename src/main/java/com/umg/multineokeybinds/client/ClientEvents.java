package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.client.KeyListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = "multineokeybinds", value = Mod.EventBusSubscriber.Bus.GAME)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            KeyListener.tick();
        }
    }
}