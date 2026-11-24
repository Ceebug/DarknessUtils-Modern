package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class LowDurabilityWarning {
    private static LowDurabilityWarning instance;
    private boolean durabilityWarningLatched;

    private LowDurabilityWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                durabilityWarningLatched = false;
                return;
            }

            if (!DarknessUtils.CONFIG.lowDurabilityWarningEnabled) {
                durabilityWarningLatched = false;
                return;
            }

            var inventory = client.player.getInventory();
            boolean hasLowDurabilityItem = false;
            int threshold = DarknessUtils.CONFIG.lowDurabilityWarningThresholdPercent;

            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                // Ignore empty, non-damageable, and completely undamaged items.
                if (stack.isEmpty() || stack.getMaxDamage() <= 0 || stack.getDamageValue() <= 0) continue;

                double remainingPercent = (double) (stack.getMaxDamage() - stack.getDamageValue())
                        / stack.getMaxDamage() * 100.0;
                // Strictly below: an item exactly at the threshold does not trigger.
                if (remainingPercent < threshold) {
                    hasLowDurabilityItem = true;
                    break;
                }
            }

            if (!hasLowDurabilityItem) {
                durabilityWarningLatched = false;
                return;
            }

            if (!durabilityWarningLatched) {
                durabilityWarningLatched = true;
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.low_durability_warning",
                                Component.keybind("key.fix")));
                WarningSound.play(client.player);
            }

            // Keep this warning latched after the key is pressed. It rearms only
            // once no inventory item is below the configured durability threshold.
            if (DarknessUtils.fixBinding.consumeClick()) {
                client.player.connection.sendCommand("fix all");
            }
        });
    }

    public static LowDurabilityWarning getInstance() {
        if (instance == null) instance = new LowDurabilityWarning();
        return instance;
    }
}
