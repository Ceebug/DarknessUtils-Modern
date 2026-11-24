package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public final class HealthWarning {
    private static HealthWarning instance;
    private boolean warnedWhileLow;

    private HealthWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                warnedWhileLow = false;
                return;
            }

            if (!DarknessUtils.CONFIG.healthWarningEnabled) {
                warnedWhileLow = false;
                return;
            }

            float maxHealth = client.player.getMaxHealth();
            if (maxHealth <= 0) {
                warnedWhileLow = false;
                return;
            }

            boolean healthIsLow = client.player.getHealth()
                    <= maxHealth * DarknessUtils.CONFIG.healthWarningThresholdPercent / 100.0;

            if (!healthIsLow) {
                warnedWhileLow = false;
                return;
            }

            if (!warnedWhileLow) {
                warnedWhileLow = true;
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.health_warning",
                                Component.keybind("key.heal")));
            }

            if (DarknessUtils.healBinding.consumeClick()) {
                client.player.connection.sendCommand("heal");
            }
        });
    }

    public static HealthWarning getInstance() {
        if (instance == null) instance = new HealthWarning();
        return instance;
    }
}
