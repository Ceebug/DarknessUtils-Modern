package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import me.ders.darknessutils.features.SlotLocking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.hud.Hud;
import net.minecraft.client.DeltaTracker;
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
        int slot = relative / 20;
        if (SlotLocking.isLocked(slot)) {
            graphics.fill(x, y, x + 16, y + 16, 0x88000000);
        }
    }
}
