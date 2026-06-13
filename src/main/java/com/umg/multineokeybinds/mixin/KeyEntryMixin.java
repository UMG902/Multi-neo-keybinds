package com.umg.multineokeybinds.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.GuiGraphics; // ADDED IMPORT
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(KeyBindsList.KeyEntry.class)
public class KeyEntryMixin {

    @Shadow @Final private KeyMapping key;
    @Shadow @Final private Button resetButton; // Shadow the button to get its position

    @Unique private Button multineo_addButton;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(KeyMapping key, Component name, CallbackInfo ci) {
        multineo_addButton = Button.builder(Component.literal("+"), btn -> {
            // Access the current screen and set the selected key
            if (Minecraft.getInstance().screen instanceof IKeyBindsScreen screen) {
                screen.setSelectedKey(this.key);
                Minecraft.getInstance().setScreen((KeyBindsScreen)screen); // Refresh
            }
        }).build();
    }

    @Inject(method = "narratables", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void addPlusButton(CallbackInfoReturnable<List<? extends NarratableEntry>> cir, List<NarratableEntry> list) {
        list.add(multineo_addButton);
        cir.setReturnValue(list);
    }

    @Inject(method = "render", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRender(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick, CallbackInfo ci) {
        // Position based on the shadowed resetButton
        int resetX = this.resetButton.getX();
        int changeX = resetX - 5 - 75;

        // Place "+" to the left of the Key (change) button
        multineo_addButton.setPosition(changeX - 5 - 20, this.resetButton.getY());
        multineo_addButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}