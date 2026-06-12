package com.umg.multineokeybinds.client;

import com.umg.multineokeybinds.core.KeyBindingManager;
import com.umg.multineokeybinds.core.KeyBindingStore;

public class ClientInit {

    public static void init() {

        KeyBindingStore.load();

        KeyBindingManager.init();
    }
}