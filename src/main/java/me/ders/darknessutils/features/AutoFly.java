package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public class AutoFly {
    private static AutoFly instance;

    private LocalPlayer previousPlayer;
    private ClientLevel previousLevel;
    private double previousX;
    private double previousY;
    private double previousZ;
    private boolean hasPreviousPosition;
    private long lastAutomaticFlyAt;

    private AutoFly() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                previousPlayer = null;
                previousLevel = null;
                hasPreviousPosition = false;
                return;
            }

            while (DarknessUtils.flyBinding.consumeClick()) {
                client.player.connection.sendCommand("fly");
            }

            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();

            boolean joinedOrRespawned = client.player != previousPlayer || client.level != previousLevel;
            double dx = x - previousX;
            double dy = y - previousY;
            double dz = z - previousZ;
            boolean teleported = hasPreviousPosition && (dx * dx + dy * dy + dz * dz) > 64.0;

            if (DarknessUtils.CONFIG.autoFlyEnabled && (joinedOrRespawned || teleported)
                    && System.currentTimeMillis() - lastAutomaticFlyAt >= 1000L) {
                client.player.connection.sendCommand("fly enable");
                lastAutomaticFlyAt = System.currentTimeMillis();
            }

            previousPlayer = client.player;
            previousLevel = client.level;
            previousX = x;
            previousY = y;
            previousZ = z;
            hasPreviousPosition = true;
        });
    }

    public static AutoFly getInstance() {
        if (instance == null) instance = new AutoFly();
        return instance;
    }
}
