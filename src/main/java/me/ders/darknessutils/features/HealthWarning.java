package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public final class HealthWarning {
    private static HealthWarning instance;
    private boolean warningConditionActive;

    private HealthWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                warningConditionActive = false;
                return;
            }

            float maxHealth = client.player.getMaxHealth();
            boolean healthBelowThreshold = DarknessUtils.CONFIG.healthWarningEnabled
                    && maxHealth > 0
                    && client.player.getHealth()
                    <= maxHealth * DarknessUtils.CONFIG.healthWarningThresholdPercent / 100.0;

            if (healthBelowThreshold && !warningConditionActive) {
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.health_warning",
                                Component.keybind("key.heal")));
            }

            // Re-evaluate every tick so the warning rearms when health recovers,
            // or immediately when the warning is disabled.
            warningConditionActive = healthBelowThreshold;

            if (healthBelowThreshold && DarknessUtils.healBinding.consumeClick()) {
                client.player.connection.sendCommand("heal");
            }
        });
    }

    public static HealthWarning getInstance() {
        if (instance == null) instance = new HealthWarning();
        return instance;
    }
}
