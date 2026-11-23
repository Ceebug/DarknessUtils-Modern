package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class AutoFly {
    private static AutoFly instance;

    private ClientPlayerEntity previousPlayer;
    private ClientWorld previousWorld;
    private double previousX;
    private double previousY;
    private double previousZ;
    private boolean hasPreviousPosition;
    private long lastAutomaticFlyAt;

    private AutoFly() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                previousPlayer = null;
                previousWorld = null;
                hasPreviousPosition = false;
                return;
            }

            while (DarknessUtils.flyBinding.wasPressed()) {
                client.player.networkHandler.sendChatCommand("fly");
            }

            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();

            boolean joinedOrRespawned = client.player != previousPlayer || client.world != previousWorld;
            double dx = x - previousX;
            double dy = y - previousY;
            double dz = z - previousZ;
            boolean teleported = hasPreviousPosition && (dx * dx + dy * dy + dz * dz) > 64.0;

            if (DarknessUtils.CONFIG.autoFlyEnabled && (joinedOrRespawned || teleported)
                    && System.currentTimeMillis() - lastAutomaticFlyAt >= 1000L) {
                client.player.networkHandler.sendChatCommand("fly enable");
                lastAutomaticFlyAt = System.currentTimeMillis();
            }

            previousPlayer = client.player;
            previousWorld = client.world;
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
