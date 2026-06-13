package com.umg.multineokeybinds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.umg.multineokeybinds.client.KeyBindsScreenExtension;
import com.umg.multineokeybinds.core.KeyBindingStore;
import com.umg.multineokeybinds.core.MultiKeyRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList.KeyEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(KeyEntry.class)
public class KeyEntryMixin {
    @Shadow @Final private KeyMapping key;
    @Shadow @Final private Component name;

    @Unique private Button multikeybinds$addButton;
    @Unique private final List<Button> multikeybinds$removeButtons = new ArrayList<>();
    @Unique private final List<Button> multikeybinds$keyButtons = new ArrayList<>();
    @Unique private final List<Integer> multikeybinds$cachedCodes = new ArrayList<>();

    @Unique
    private void multikeybinds$syncButtons() {
        List<Integer> currentCodes = new ArrayList<>(MultiKeyRegistry.get(this.key));

        if (!currentCodes.equals(this.multikeybinds$cachedCodes)) {
            this.multikeybinds$cachedCodes.clear();
            this.multikeybinds$cachedCodes.addAll(currentCodes);
            this.multikeybinds$removeButtons.clear();
            this.multikeybinds$keyButtons.clear();

            for (Integer keyCode : currentCodes) {
                InputConstants.Key inputKey = InputConstants.Type.KEYSYM.getOrCreate(keyCode);

                Button keyBtn = Button.builder(
                        inputKey.getDisplayName(),
                        b -> {
                            if (Minecraft.getInstance().screen instanceof KeyBindsScreenExtension ext) {
                                ext.multikeybinds$startListening(this.key, keyCode);
                            }
                        }
                ).bounds(0, 0, 75, 20).build();

                Button removeBtn = Button.builder(
                        Component.literal("✕"),
                        b -> {
                            MultiKeyRegistry.remove(this.key, keyCode);
                            KeyBindingStore.save();
                            Minecraft.getInstance().options.save();
                            this.multikeybinds$syncButtons();
                        }
                ).bounds(0, 0, 20, 20).build();

                this.multikeybinds$keyButtons.add(keyBtn);
                this.multikeybinds$removeButtons.add(removeBtn);
            }
        }

        if (this.multikeybinds$addButton == null) {
            this.multikeybinds$addButton = Button.builder(
                    Component.literal("+"),
                    b -> {
                        if (Minecraft.getInstance().screen instanceof KeyBindsScreenExtension ext) {
                            ext.multikeybinds$startListening(this.key);
                        }
                    }
            ).bounds(0, 0, 20, 20).build();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void multikeybinds$renderCustom(GuiGraphics gg, int idx, int top, int left, int width,
                                            int height, int mouseX, int mouseY, boolean hovering, float pt,
                                            CallbackInfo ci) {
        this.multikeybinds$syncButtons();

        int rightEdge = left + width;
        int addX = rightEdge - 25;
        int baseY = top - 2;

        gg.drawString(Minecraft.getInstance().font, this.name, left, top + 6, 0xFFFFFF);

        if (this.multikeybinds$addButton != null) {
            this.multikeybinds$addButton.setPosition(addX, baseY);
            this.multikeybinds$addButton.render(gg, mouseX, mouseY, pt);
        }

        int keyX = addX - 25 - 75;
        int removeX = keyX - 22;

        for (int i = 0; i < this.multikeybinds$keyButtons.size(); i++) {
            int rowY = baseY + (i * 20);

            Button keyBtn = this.multikeybinds$keyButtons.get(i);
            Button removeBtn = this.multikeybinds$removeButtons.get(i);

            keyBtn.setPosition(keyX, rowY);
            keyBtn.render(gg, mouseX, mouseY, pt);

            removeBtn.setPosition(removeX, rowY);
            removeBtn.render(gg, mouseX, mouseY, pt);
        }
    }

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void multikeybinds$children(CallbackInfoReturnable<List<? extends GuiEventListener>> cir) {
        this.multikeybinds$syncButtons();
        List<GuiEventListener> list = new ArrayList<>(cir.getReturnValue());
        if (this.multikeybinds$addButton != null) {
            list.add(this.multikeybinds$addButton);
        }
        list.addAll(this.multikeybinds$keyButtons);
        list.addAll(this.multikeybinds$removeButtons);
        cir.setReturnValue(list);
    }

    @Inject(method = "narratables", at = @At("RETURN"), cancellable = true)
    private void multikeybinds$narratables(CallbackInfoReturnable<List<? extends NarratableEntry>> cir) {
        this.multikeybinds$syncButtons();
        List<NarratableEntry> list = new ArrayList<>(cir.getReturnValue());
        if (this.multikeybinds$addButton != null) {
            list.add(this.multikeybinds$addButton);
        }
        list.addAll(this.multikeybinds$keyButtons);
        list.addAll(this.multikeybinds$removeButtons);
        cir.setReturnValue(list);
    }
}
