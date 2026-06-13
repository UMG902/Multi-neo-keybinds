package com.umg.multineokeybinds.client;

import net.minecraft.client.KeyMapping;

public interface KeyBindsScreenExtension {
    void multikeybinds$startListening(KeyMapping mapping);
    void multikeybinds$startListening(KeyMapping mapping, Integer keyToReplace);
}
