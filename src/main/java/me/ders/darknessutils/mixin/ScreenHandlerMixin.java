package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.ders.darknessutils.features.SlotLocking.isLocked;

@Mixin(AbstractContainerMenu.class)
public abstract class ScreenHandlerMixin {
    @Shadow @Final public NonNullList<Slot> slots;

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void darknessutils$blockLockedSlotClick(int slotIndex, int buttonNum, ContainerInput input,
                                                    Player player, CallbackInfo ci) {
        if (!DarknessUtils.CONFIG.doSlotLocking()
                || slotIndex < 0
                || slotIndex >= slots.size()) {
            return;
        }

        Slot slot = slots.get(slotIndex);
        if (slot.container == player.getInventory() && isLocked(slot.getContainerSlot())) {
            ci.cancel();
        }
    }
}
