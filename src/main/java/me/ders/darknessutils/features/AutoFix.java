package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.item.ItemStack;

public class AutoFix {
    private static AutoFix instance;
    private long lastAutomaticFixAt;

    private AutoFix() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            while (DarknessUtils.fixBinding.consumeClick()) {
                client.player.connection.sendCommand(DarknessUtils.CONFIG.manualFixAll ? "fix all" : "fix");
            }

            if (!DarknessUtils.CONFIG.autoFixEnabled) return;
            if (System.currentTimeMillis() - lastAutomaticFixAt
                    < DarknessUtils.CONFIG.autoFixCooldownSeconds * 1000L) return;

            var inventory = client.player.getInventory();
            boolean needsFix = false;
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (stack.isEmpty() || stack.getMaxDamage() <= 0) continue;

                double remainingPercent = (double) (stack.getMaxDamage() - stack.getDamageValue())
                        / stack.getMaxDamage() * 100.0;
                if (remainingPercent <= DarknessUtils.CONFIG.autoFixThresholdPercent) {
                    needsFix = true;
                    break;
                }
            }

            if (needsFix) {
                client.player.connection.sendCommand("fix");
                lastAutomaticFixAt = System.currentTimeMillis();
            }
        });
    }

    public static AutoFix getInstance() {
        if (instance == null) instance = new AutoFix();
        return instance;
    }
}
