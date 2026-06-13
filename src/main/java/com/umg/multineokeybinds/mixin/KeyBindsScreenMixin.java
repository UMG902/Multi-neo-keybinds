package com.umg.multineokeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.client.KeyBindsScreenExtension;
import com.umg.multineokeybinds.core.KeyBindingStore;
import com.umg.multineokeybinds.core.MultiKeyRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsScreen.class)
public class KeyBindsScreenMixin implements KeyBindsScreenExtension {

    @Shadow public KeyMapping selectedKey;
    @Shadow private KeyBindsList keyBindsList;

    @Unique private KeyMapping multikeybinds$current = null;
    @Unique private boolean multikeybinds$listening = false;
    @Unique private Integer multikeybinds$keyToReplace = null;

    @Override
    public void multikeybinds$startListening(KeyMapping mapping) {
        this.multikeybinds$current = mapping;
        this.multikeybinds$listening = true;
        this.multikeybinds$keyToReplace = null;
    }

    @Override
    public void multikeybinds$startListening(KeyMapping mapping, Integer keyToReplace) {
        this.multikeybinds$current = mapping;
        this.multikeybinds$listening = true;
        this.multikeybinds$keyToReplace = keyToReplace;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void multikeybinds$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.multikeybinds$listening) return;

        if (keyCode == 256) { // Escape
            this.multikeybinds$listening = false;
            this.multikeybinds$current = null;
            this.multikeybinds$keyToReplace = null;
            this.selectedKey = null;
            cir.setReturnValue(true);
            return;
        }

        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (this.multikeybinds$current != null) {
            if (this.multikeybinds$keyToReplace != null) {
                MultiKeyRegistry.remove(this.multikeybinds$current, this.multikeybinds$keyToReplace);
            }
            MultiKeyRegistry.add(this.multikeybinds$current, key.getValue());

            // Saves instantly the moment you press a key!
            KeyBindingStore.save();
            Minecraft.getInstance().options.save();

            this.selectedKey = null;
            this.keyBindsList.resetMappingAndUpdateButtons();
        }

        this.multikeybinds$listening = false;
        this.multikeybinds$current = null;
        this.multikeybinds$keyToReplace = null;
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void multikeybinds$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.multikeybinds$listening) {
            if (this.multikeybinds$current != null) {
                if (this.multikeybinds$keyToReplace != null) {
                    MultiKeyRegistry.remove(this.multikeybinds$current, this.multikeybinds$keyToReplace);
                }
                InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
                MultiKeyRegistry.add(this.multikeybinds$current, key.getValue());

                // Saves instantly the moment you click a mouse button!
                KeyBindingStore.save();
                Minecraft.getInstance().options.save();

                this.selectedKey = null;
                this.keyBindsList.resetMappingAndUpdateButtons();
            }

            this.multikeybinds$listening = false;
            this.multikeybinds$current = null;
            this.multikeybinds$keyToReplace = null;
            cir.setReturnValue(true);
        }
    }
}