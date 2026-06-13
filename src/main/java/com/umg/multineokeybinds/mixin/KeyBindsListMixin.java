package com.umg.multineokeybinds.mixin;

import com.umg.multineokeybinds.ExtraKeyEntry;
import com.umg.multineokeybinds.config.KeybindManager;
import com.umg.multineokeybinds.mixin.IKeyBindsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin; // MISSING IMPORT ADDED
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(KeyBindsList.class)
public abstract class KeyBindsListMixin implements IKeyBindsList {

    @Unique private boolean multineo_isAdding = false;
    @Unique private KeyMapping multineo_targetMapping = null;
    @Unique private Integer multineo_editingOldKey = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        ArrayList<KeyBindsList.Entry> children = new ArrayList<>(this.getChildren());

        for (int i = children.size() - 1; i >= 0; i--) {
            KeyBindsList.Entry entry = children.get(i);
            if (entry instanceof KeyBindsList.KeyEntry keyEntry) {
                KeyMapping mapping = ((KeyEntryAccessor) keyEntry).getKey();

                if (mapping != null) {
                    java.util.List<Integer> extras = KeybindManager.getExtraKeys(mapping.getName());
                    if (!extras.isEmpty()) {
                        int insertIndex = i + 1;
                        for (int code : extras) {
                            children.add(insertIndex, new ExtraKeyEntry(this.getKeyBindsScreen(), mapping, code, (KeyBindsList)(Object)this));
                            insertIndex++;
                        }
                    }
                }
            }
        }
        this.setChildren(children);
    }

    @Unique
    @Override
    public void startAddingExtra(KeyMapping mapping) {
        this.multineo_isAdding = true;
        this.multineo_targetMapping = mapping;
        this.multineo_editingOldKey = null;
    }

    @Unique
    @Override
    public void startEditingExtra(KeyMapping mapping, int oldKey) {
        this.multineo_isAdding = true;
        this.multineo_targetMapping = mapping;
        this.multineo_editingOldKey = oldKey;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfo ci) {
        if (multineo_isAdding && multineo_targetMapping != null) {
            if (keyCode == 256) {
                multineo_isAdding = false;
                multineo_targetMapping = null;
                ci.cancel();
                return;
            }

            if (multineo_editingOldKey != null) {
                KeybindManager.updateExtraKey(multineo_targetMapping.getName(), multineo_editingOldKey, keyCode);
            } else {
                KeybindManager.addExtraKey(multineo_targetMapping.getName(), keyCode);
            }

            Minecraft.getInstance().setScreen(this.getKeyBindsScreen());

            multineo_isAdding = false;
            multineo_targetMapping = null;
            multineo_editingOldKey = null;

            ci.cancel();
        }
    }
}