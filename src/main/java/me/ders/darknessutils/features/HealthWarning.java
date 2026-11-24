package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public final class HealthWarning {
    private static HealthWarning instance;
    private boolean healthWarningLatched;
    private boolean waitingForConfirmation;
    private boolean confirmationReceived;
    private int confirmationTicks;

    private HealthWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                resetState();
                return;
            }

            if (!DarknessUtils.CONFIG.healthWarningEnabled) {
                resetState();
                return;
            }

            float maxHealth = client.player.getMaxHealth();
            if (maxHealth <= 0) {
                resetState();
                return;
            }

            boolean healthIsLow = client.player.getHealth()
                    <= maxHealth * DarknessUtils.CONFIG.healthWarningThresholdPercent / 100.0;

            if (waitingForConfirmation) {
                if (!confirmationReceived) {
                    confirmationTicks++;
                }

                // A confirmation is preferred, but allow natural regeneration to
                // rearm the warning if no response arrives within three ticks.
                if ((confirmationReceived || confirmationTicks >= 3) && !healthIsLow) {
                    resetState();
                }
            } else if (!healthIsLow) {
                healthWarningLatched = false;
            }

            if (!healthWarningLatched) {
                healthWarningLatched = true;
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.health_warning",
                                Component.keybind("key.heal")));
                WarningSound.play(client.player);
            }

            if (DarknessUtils.healBinding.consumeClick()) {
                waitingForConfirmation = true;
                confirmationReceived = false;
                confirmationTicks = 0;
                client.player.connection.sendCommand("heal");
            }
        });
    }

    public void onHealedMessage() {
        if (waitingForConfirmation) {
            confirmationReceived = true;
        }
    }

    private void resetState() {
        healthWarningLatched = false;
        waitingForConfirmation = false;
        confirmationReceived = false;
        confirmationTicks = 0;
    }

    public static HealthWarning getInstance() {
        if (instance == null) instance = new HealthWarning();
        return instance;
    }
}
