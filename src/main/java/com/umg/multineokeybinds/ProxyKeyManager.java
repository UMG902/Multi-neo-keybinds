package com.umg.multineokeybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyKeyManager {
    public static final int MAX_PROXIES = 20;
    public static final String BASE_CATEGORY = "key.categories.multineokeybinds";

    public static final ProxyKeyMapping[] PROXY_KEYS = new ProxyKeyMapping[MAX_PROXIES];
    private static final Map<KeyMapping, KeyMapping> activeProxies = new ConcurrentHashMap<>();

    /** Custom KeyMapping class to trick vanilla sorting and categorization */
    public static class ProxyKeyMapping extends KeyMapping {
        public ProxyKeyMapping(int id) {
            super("key.multineokeybinds.extra_" + id, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), BASE_CATEGORY);
        }

        @Override
        public String getCategory() {
            // Crucial: Inherit the target's category so vanilla UI groups them together
            KeyMapping target = getTargetFor(this);
            return target != null ? target.getCategory() : super.getCategory();
        }
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        for (int i = 0; i < MAX_PROXIES; i++) {
            PROXY_KEYS[i] = new ProxyKeyMapping(i + 1);
            event.register(PROXY_KEYS[i]);
        }
    }

    public static boolean linkTargetKey(KeyMapping target) {
        for (ProxyKeyMapping proxy : PROXY_KEYS) {
            if (!activeProxies.containsKey(proxy)) {
                activeProxies.put(proxy, target);
                return true;
            }
        }
        return false;
    }

    public static void unlinkProxy(KeyMapping proxy) {
        activeProxies.remove(proxy);
        // Reset the key to UNBOUND when unlinked so it doesn't accidentally trigger things later
        proxy.setKeyModifierAndCode(net.neoforged.neoforge.client.settings.KeyModifier.NONE, InputConstants.UNKNOWN);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        for (Map.Entry<KeyMapping, KeyMapping> entry : activeProxies.entrySet()) {
            KeyMapping proxy = entry.getKey();
            KeyMapping target = entry.getValue();

            while (proxy.consumeClick()) {
                KeyMapping.click(target.getKey());
            }

            if (proxy.isDown()) {
                target.setDown(true);
            } else {
                long window = mc.getWindow().getWindow();
                if (!InputConstants.isKeyDown(window, target.getKey().getValue())) {
                    target.setDown(false);
                }
            }
        }
    }

    /* --- Helper Methods for UI Mixins --- */

    public static boolean isProxy(KeyMapping mapping) {
        return mapping instanceof ProxyKeyMapping;
    }

    public static KeyMapping getTargetFor(KeyMapping proxy) {
        return activeProxies.get(proxy);
    }

    public static List<KeyMapping> getProxiesFor(KeyMapping target) {
        List<KeyMapping> list = new ArrayList<>();
        for (ProxyKeyMapping proxy : PROXY_KEYS) {
            if (activeProxies.get(proxy) == target) {
                list.add(proxy);
            }
        }
        return list;
    }

    public static KeyMapping getMappingByName(String name) {
        for (ProxyKeyMapping k : PROXY_KEYS) {
            if (k.getName().equals(name)) return k;
        }
        return null;
    }
}