package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import me.ders.darknessutils.features.SlotLocking;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {
    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void darknessutils$drawLockedHotbarOverlay(GuiGraphicsExtractor graphics, int x, int y,
                                                        DeltaTracker deltaTracker, Player player,
                                                        ItemStack stack, int seed, CallbackInfo ci) {
        if (!DarknessUtils.CONFIG.doSlotLocking()) return;
        int firstSlotX = graphics.guiWidth() / 2 - 90 + 2;
        int relative = x - firstSlotX;
        if (relative < 0 || relative > 8 * 20 || relative % 20 != 0) return;
        if (SlotLocking.isLocked(relative / 20)) {
            graphics.fill(x, y, x + 16, y + 16, 0x88000000);
        }
    }
}
