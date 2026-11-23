package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.ders.darknessutils.features.SlotLocking.isLocked;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow @Final public Container container;
    @Shadow public abstract int getContainerSlot();

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void darknessutils$preventPlacingIntoLockedSlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null
                && container == client.player.getInventory()
                && DarknessUtils.CONFIG.doSlotLocking()
                && isLocked(getContainerSlot())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void darknessutils$preventTakingFromLockedSlot(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (container == player.getInventory()
                && DarknessUtils.CONFIG.doSlotLocking()
                && isLocked(getContainerSlot())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "allowModification", at = @At("HEAD"), cancellable = true)
    private void darknessutils$preventModifyingLockedSlot(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (container == player.getInventory()
                && DarknessUtils.CONFIG.doSlotLocking()
                && isLocked(getContainerSlot())) {
            cir.setReturnValue(false);
        }
    }
}
