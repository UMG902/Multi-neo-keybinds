package com.umg.multineokeybinds.mixin;

import com.umg.multineokeybinds.ProxyKeyManager;
import com.umg.multineokeybinds.mixin.OptionsSubScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.minecraft.client.gui.screens.options.controls.KeyBindsList$KeyEntry")
public class KeyEntryMixin {

    @Final @Shadow private KeyMapping key;
    private Button proxyButton;

    // Omitting the parameters and just using CallbackInfo avoids inner-class signature mismatch
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        boolean isProxy = ProxyKeyManager.isProxy(this.key);
        Component btnText = isProxy ? Component.literal("-") : Component.literal("+");

        this.proxyButton = Button.builder(btnText, b -> {
            boolean changed = false;

            if (isProxy) {
                ProxyKeyManager.unlinkProxy(this.key);
                changed = true;
            } else {
                changed = ProxyKeyManager.linkTargetKey(this.key);
            }

            // If mapping changed, seamlessly restart the screen to refresh the list
            if (changed) {
                Minecraft mc = Minecraft.getInstance();

                // We run this on the next tick so we aren't modifying the screen
                // while the mouse-click event is still being processed.
                mc.execute(() -> {
                    if (mc.screen instanceof KeyBindsScreen kbs) {
                        // Simply re-run the init() method on the current screen instance.
                        // This forces Minecraft to rebuild the KeyBindsList and refresh the UI.
                        kbs.init(mc, kbs.width, kbs.height);
                    }
                });
            }
        }).bounds(0, 0, 20, 20).build();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick, CallbackInfo ci) {
        this.proxyButton.setX(left + 225);
        this.proxyButton.setY(top);
        this.proxyButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Safely add our button to the vanilla event listeners list so it can be clicked
    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void onChildren(CallbackInfoReturnable<List<? extends GuiEventListener>> cir) {
        List<GuiEventListener> list = new ArrayList<>(cir.getReturnValue());
        list.add(this.proxyButton);
        cir.setReturnValue(list);
    }

    // Safely add our button to the narratables list to prevent background narrator crashes
    @Inject(method = "narratables", at = @At("RETURN"), cancellable = true)
    private void onNarratables(CallbackInfoReturnable<List<? extends NarratableEntry>> cir) {
        List<NarratableEntry> list = new ArrayList<>(cir.getReturnValue());
        list.add(this.proxyButton);
        cir.setReturnValue(list);
    }
}