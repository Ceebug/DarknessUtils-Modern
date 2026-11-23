package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.ders.darknessutils.features.SlotLocking.isLocked;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {
    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void darknessutils$drawLockedSlotOverlay(GuiGraphicsExtractor graphics, Slot slot,
                                                      int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !DarknessUtils.CONFIG.doSlotLocking()) {
            return;
        }

        Inventory inventory = client.player.getInventory();
        if (slot.container == inventory && isLocked(slot.getSlotIndex())) {
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x88000000);
        }
    }
}
