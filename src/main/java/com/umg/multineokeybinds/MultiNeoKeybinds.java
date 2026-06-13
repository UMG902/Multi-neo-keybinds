package com.umg.multineokeybinds;

import com.umg.multineokeybinds.config.KeybindManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.InputEvent; // FIX: Moved to client.event

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class MultiNeoKeybinds {
    public static final String MODID = "multineokeybinds";

    public MultiNeoKeybinds(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        KeybindManager.load();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        KeybindManager.handleKeyPress(event);
    }
}