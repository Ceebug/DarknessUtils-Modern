package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class LowDurabilityWarning {
    private static LowDurabilityWarning instance;
    private boolean warningConditionActive;

    private LowDurabilityWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                warningConditionActive = false;
                return;
            }

            boolean hasItemBelowThreshold = false;
            if (DarknessUtils.CONFIG.lowDurabilityWarningEnabled) {
                var inventory = client.player.getInventory();
                int threshold = DarknessUtils.CONFIG.lowDurabilityWarningThresholdPercent;

                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    ItemStack stack = inventory.getItem(slot);
                    if (stack.isEmpty() || stack.getMaxDamage() <= 0 || stack.getDamageValue() <= 0) {
                        continue;
                    }

                    double remainingPercent = (double) (stack.getMaxDamage() - stack.getDamageValue())
                            / stack.getMaxDamage() * 100.0;
                    if (remainingPercent < threshold) {
                        hasItemBelowThreshold = true;
                        break;
                    }
                }
            }

            if (hasItemBelowThreshold && !warningConditionActive) {
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.low_durability_warning",
                                Component.keybind("key.fix")));
            }

            // Re-evaluate the condition every tick. This rearms the warning as soon
            // as no inventory item is below the configured threshold.
            warningConditionActive = hasItemBelowThreshold;

            // The action key remains usable while the condition is active; pressing
            // it does not change the warning's condition state.
            if (hasItemBelowThreshold && DarknessUtils.fixBinding.consumeClick()) {
                client.player.connection.sendCommand("fix all");
            }
        });
    }

    public static LowDurabilityWarning getInstance() {
        if (instance == null) instance = new LowDurabilityWarning();
        return instance;
    }
}
