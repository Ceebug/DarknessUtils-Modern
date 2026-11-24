package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public final class HealthWarning {
    private static HealthWarning instance;
    private boolean triggerHeal;

    private HealthWarning() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                triggerHeal = false;
                return;
            }

            if (triggerHeal) {
                if (DarknessUtils.healBinding.consumeClick()) {
                    client.player.connection.sendCommand("heal");
                    triggerHeal = false;
                }
                return;
            }

            if (!DarknessUtils.CONFIG.healthWarningEnabled) return;

            if (client.player.getHealth() <= DarknessUtils.CONFIG.healthWarningThreshold) {
                triggerHeal = true;
                client.player.displayClientMessage(
                        Component.translatable("message.darknessutils.health_warning",
                                Component.keybind("key.heal")), false);
            }
        });
    }

    public static HealthWarning getInstance() {
        if (instance == null) instance = new HealthWarning();
        return instance;
    }
}
