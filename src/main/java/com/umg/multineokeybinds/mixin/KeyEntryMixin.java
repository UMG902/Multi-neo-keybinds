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

    @Unique private Button multikeybinds$addButton;
    @Unique private final List<Button> multikeybinds$bindButtons = new ArrayList<>();
    @Unique private final List<Button> multikeybinds$removeButtons = new ArrayList<>();
    @Unique private final List<Integer> multikeybinds$cachedCodes = new ArrayList<>();

    @Unique
    private void multikeybinds$syncButtons() {
        if (this.multikeybinds$addButton == null) {
            this.multikeybinds$addButton = Button.builder(Component.literal("+"), b -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof KeyBindsScreenExtension ext) {
                    ext.multikeybinds$startListening(this.key);
                }
            }).bounds(0, 0, 20, 20).build();
        }

        List<Integer> currentCodes = new ArrayList<>(MultiKeyRegistry.get(this.key));

        if (!currentCodes.equals(this.multikeybinds$cachedCodes)) {
            this.multikeybinds$cachedCodes.clear();
            this.multikeybinds$cachedCodes.addAll(currentCodes);

            this.multikeybinds$bindButtons.clear();
            this.multikeybinds$removeButtons.clear();

            for (Integer code : currentCodes) {
                final int keyCode = code;
                InputConstants.Key inputKey = InputConstants.Type.KEYSYM.getOrCreate(keyCode);

                Button bindButton = Button.builder(
                        Component.literal(inputKey.getName()),
                        b -> {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.screen instanceof KeyBindsScreenExtension ext) {
                                // Pass the key code to replace when rebinding
                                ext.multikeybinds$startListening(this.key, keyCode);
                            }
                        }
                ).bounds(0, 0, 50, 20).build();

                Button removeButton = Button.builder(
                        Component.literal("x"),
                        b -> {
                            MultiKeyRegistry.remove(this.key, keyCode);
                            KeyBindingStore.save();
                            Minecraft.getInstance().options.save();
                        }
                ).bounds(0, 0, 20, 20).build();

                this.multikeybinds$bindButtons.add(bindButton);
                this.multikeybinds$removeButtons.add(removeButton);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void multikeybinds$renderExtraButtons(
            GuiGraphics graphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean hovering,
            float partialTick,
            CallbackInfo ci
    ) {
        this.multikeybinds$syncButtons();

        // Position buttons to the left of the entry, not right
        int addX = left + width - 156;
        int y = top - 2;

        this.multikeybinds$addButton.setPosition(addX, y);
        this.multikeybinds$addButton.render(graphics, mouseX, mouseY, partialTick);

        int x = left + width - 211;

        for (int i = 0; i < this.multikeybinds$bindButtons.size(); i++) {
            Button bindButton = this.multikeybinds$bindButtons.get(i);
            Button removeButton = this.multikeybinds$removeButtons.get(i);

            bindButton.setPosition(x, y);
            bindButton.render(graphics, mouseX, mouseY, partialTick);

            removeButton.setPosition(x - 22, y);
            removeButton.render(graphics, mouseX, mouseY, partialTick);

            x -= 77;
        }
    }

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void multikeybinds$children(CallbackInfoReturnable<List<? extends GuiEventListener>> cir) {
        this.multikeybinds$syncButtons();

        List<GuiEventListener> list = new ArrayList<>(cir.getReturnValue());
        list.add(this.multikeybinds$addButton);
        list.addAll(this.multikeybinds$bindButtons);
        list.addAll(this.multikeybinds$removeButtons);
        cir.setReturnValue(list);
    }

    @Inject(method = "narratables", at = @At("RETURN"), cancellable = true)
    private void multikeybinds$narratables(CallbackInfoReturnable<List<? extends NarratableEntry>> cir) {
        this.multikeybinds$syncButtons();

        List<NarratableEntry> list = new ArrayList<>(cir.getReturnValue());
        list.add(this.multikeybinds$addButton);
        list.addAll(this.multikeybinds$bindButtons);
        list.addAll(this.multikeybinds$removeButtons);
        cir.setReturnValue(list);
    }
}
