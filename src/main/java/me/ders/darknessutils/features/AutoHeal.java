package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AutoHeal {
    private static AutoHeal instance;
    private long lastAutomaticHealAt;

    private AutoHeal() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            while (DarknessUtils.healBinding.consumeClick()) {
                client.player.connection.sendCommand("heal");
            }

            if (!DarknessUtils.CONFIG.autoHealEnabled) return;
            if (System.currentTimeMillis() - lastAutomaticHealAt
                    < DarknessUtils.CONFIG.autoHealCooldownSeconds * 1000L) return;

            if (client.player.getHealth() <= DarknessUtils.CONFIG.autoHealThreshold) {
                client.player.connection.sendCommand("heal");
                lastAutomaticHealAt = System.currentTimeMillis();
            }
        });
    }

    public static AutoHeal getInstance() {
        if (instance == null) instance = new AutoHeal();
        return instance;
    }
}
