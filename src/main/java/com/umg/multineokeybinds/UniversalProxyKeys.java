package com.umg.multineokeybinds;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(UniversalProxyKeys.MODID)
public class UniversalProxyKeys {
    public static final String MODID = "multineokeybinds";

    public UniversalProxyKeys(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ProxyKeyManager::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(ProxyKeyManager::onClientTick);
    }
}