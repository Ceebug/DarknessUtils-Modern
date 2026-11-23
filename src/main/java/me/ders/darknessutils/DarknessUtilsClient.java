package me.ders.darknessutils;

import me.ders.darknessutils.config.DarknessConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;

public class DarknessUtilsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(ClientCommandManager.literal("duconfig")
                    .executes(context -> openConfig(context.getSource().getClient())));
            dispatcher.register(ClientCommandManager.literal("darknessutilsconfig")
                    .executes(context -> openConfig(context.getSource().getClient())));
        });
    }

    private static int openConfig(Minecraft client) {
        client.setScreen(DarknessConfigScreen.create(client.screen));
        return 1;
    }
}
