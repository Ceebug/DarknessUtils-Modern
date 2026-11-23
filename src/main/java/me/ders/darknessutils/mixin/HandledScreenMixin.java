package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import me.ders.darknessutils.features.SlotLocking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void darknessutils$toggleLockedSlot(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!DarknessUtils.CONFIG.doSlotLocking()
                || DarknessUtils.lockBinding == null
                || !DarknessUtils.lockBinding.matches(event)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Slot slot = ((HandledScreenAccessor) this).darknessutils$getHoveredSlot();
        if (slot != null && slot.container == client.player.getInventory()) {
            SlotLocking.toggleSlot(slot.index);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void darknessutils$drawLockedSlotOverlay(GuiGraphicsExtractor graphics, Slot slot,
                                                      int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !DarknessUtils.CONFIG.doSlotLocking()) return;
        Inventory inventory = client.player.getInventory();
        if (slot.container == inventory && SlotLocking.isLocked(slot.index)) {
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x88000000);
        }
    }

}
