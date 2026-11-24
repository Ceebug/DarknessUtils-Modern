package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

public final class HealthWarning {
    private static final Pattern HEALED_MESSAGE = Pattern.compile("\\bhealed\\b", Pattern.CASE_INSENSITIVE);

    private static HealthWarning instance;
    private boolean healthWarningLatched;
    private boolean awaitingHealConfirmation;

    private HealthWarning() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (awaitingHealConfirmation && HEALED_MESSAGE.matcher(message.getString()).find()) {
                awaitingHealConfirmation = false;
                healthWarningLatched = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                healthWarningLatched = false;
                awaitingHealConfirmation = false;
                return;
            }

            if (!DarknessUtils.CONFIG.healthWarningEnabled) {
                healthWarningLatched = false;
                awaitingHealConfirmation = false;
                return;
            }

            float maxHealth = client.player.getMaxHealth();
            if (maxHealth <= 0) {
                return;
            }

            boolean healthIsLow = client.player.getHealth()
                    <= maxHealth * DarknessUtils.CONFIG.healthWarningThresholdPercent / 100.0;

            if (healthIsLow && !healthWarningLatched) {
                healthWarningLatched = true;
                client.player.sendSystemMessage(
                        Component.translatable("message.darknessutils.health_warning",
                                Component.keybind("key.heal")));
                WarningSound.play(client.player);
            }

            if (DarknessUtils.healBinding.consumeClick() && !awaitingHealConfirmation) {
                awaitingHealConfirmation = true;
                client.player.connection.sendCommand("heal");
            }
        });
    }

    public static HealthWarning getInstance() {
        if (instance == null) instance = new HealthWarning();
        return instance;
    }
}
