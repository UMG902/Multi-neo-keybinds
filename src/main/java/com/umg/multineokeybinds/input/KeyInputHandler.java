package com.umg.multineokeybinds.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.core.KeyBindingStore;
import com.umg.multineokeybinds.core.MultiKeyRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class KeyInputHandler {
    private KeyInputHandler() {
    }

    public static void bind(KeyMapping mapping, InputConstants.Key key) {
        if (mapping == null || key == null) {
            return;
        }

        MultiKeyRegistry.add(mapping, key.getValue());
        KeyBindingStore.save();
        Minecraft.getInstance().options.save();
    }

    public static void replace(KeyMapping mapping, int oldKeyCode, InputConstants.Key key) {
        if (mapping == null || key == null) {
            return;
        }

        MultiKeyRegistry.remove(mapping, oldKeyCode);
        MultiKeyRegistry.add(mapping, key.getValue());
        KeyBindingStore.save();
        Minecraft.getInstance().options.save();
    }
}
