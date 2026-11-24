package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class LowDurabilityWarning {
    private static LowDurabilityWarning instance;
    private boolean durabilityWarningLatched;
    private boolean waitingForConfirmation;
    private boolean confirmationReceived;
    private int confirmationTicks;

    private LowDurabilityWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                resetState();
                return;
            }

            if (!DarknessUtils.CONFIG.lowDurabilityWarningEnabled) {
                resetState();
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

            if (waitingForConfirmation) {
                if (!confirmationReceived) {
                    confirmationTicks++;
                }

                // If the server does not reply, removing the item or repairing it
                // locally still clears the warning after the three-tick grace period.
                if ((confirmationReceived || confirmationTicks >= 3) && !hasLowDurabilityItem) {
                    resetState();
                }
            } else if (!hasLowDurabilityItem) {
                durabilityWarningLatched = false;
            }

            if (!durabilityWarningLatched) {
                durabilityWarningLatched = true;
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.low_durability_warning",
                                Component.keybind("key.fix")));
                WarningSound.play(client.player);
            }

            if (DarknessUtils.fixBinding.consumeClick()) {
                waitingForConfirmation = true;
                confirmationReceived = false;
                confirmationTicks = 0;
                client.player.connection.sendCommand("fix all");
            }
        });
    }

    public void onFixedMessage() {
        if (waitingForConfirmation) {
            confirmationReceived = true;
        }
    }

    private void resetState() {
        durabilityWarningLatched = false;
        waitingForConfirmation = false;
        confirmationReceived = false;
        confirmationTicks = 0;
    }

    public static LowDurabilityWarning getInstance() {
        if (instance == null) instance = new LowDurabilityWarning();
        return instance;
    }
}
