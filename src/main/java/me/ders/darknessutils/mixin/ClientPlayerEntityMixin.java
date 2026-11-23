package me.ders.darknessutils.mixin;

import me.ders.darknessutils.features.SlotLocking;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void darknessutils$preventDroppingLockedSelectedItem(LocalPlayer player, boolean dropAll, CallbackInfo ci) {
        if (SlotLocking.isLocked(player.getInventory().getSelectedSlot())) {
            ci.cancel();
        }
    }
}
