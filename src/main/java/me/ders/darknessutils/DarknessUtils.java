package me.ders.darknessutils;

import com.mojang.blaze3d.platform.InputConstants;
import me.ders.darknessutils.config.DarknessConfigScreen;
import me.ders.darknessutils.config.DarknessModSettings;
import me.ders.darknessutils.features.AutoFix;
import me.ders.darknessutils.features.AutoFly;
import me.ders.darknessutils.features.AutoHeal;
import me.ders.darknessutils.features.SlotLocking;
import net.fabricmc.api.ClientModInitializer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;

public class DarknessUtils implements ClientModInitializer {
    public static final DarknessModSettings CONFIG = DarknessModSettings.createAndLoad();

    public static AutoFix autoFix;
    public static AutoHeal autoHeal;
    public static AutoFly autoFly;

    public static KeyMapping lockBinding;
    public static KeyMapping fixBinding;
    public static KeyMapping healBinding;
    public static KeyMapping flyBinding;

    public static boolean isSaveDirty = false;
    private long lastDirtyCheck = System.currentTimeMillis();
    public static LinkedHashSet<Integer> lockedSlots = new LinkedHashSet<>();

    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("darknessutils", "main")
    );

    @Override
    public void onInitializeClient() {
        lockBinding = registerKey("key.slotlock", InputConstants.KEY_LALT);
        fixBinding = registerKey("key.fix", InputConstants.KEY_F);
        healBinding = registerKey("key.heal", InputConstants.KEY_H);
        flyBinding = registerKey("key.fly", InputConstants.KEY_V);

        autoFix = AutoFix.getInstance();
        autoHeal = AutoHeal.getInstance();
        autoFly = AutoFly.getInstance();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SlotLocking.handleJoinWorld();
            org.spongepowered.asm.mixin.MixinEnvironment.getCurrentEnvironment().audit();
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("duconfig")
                    .executes(context -> openConfig(context.getSource().getClient())));
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("darknessutilsconfig")
                    .executes(context -> openConfig(context.getSource().getClient())));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long now = System.currentTimeMillis();
            if (now - lastDirtyCheck <= 2000) return;

            if (isSaveDirty) {
                Path slotLockPath = FabricLoader.getInstance().getGameDir().resolve("slotlock.json");
                String json = "{ }";
                try {
                    if (Files.exists(slotLockPath)) json = Files.readString(slotLockPath);
                } catch (Exception ignored) {
                }

                com.google.gson.JsonObject jsonObject =
                        new com.google.gson.Gson().fromJson(json, com.google.gson.JsonObject.class);
                if (jsonObject == null) jsonObject = new com.google.gson.JsonObject();
                com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
                lockedSlots.forEach(jsonArray::add);
                jsonObject.add("lockedSlots", jsonArray);

                try {
                    Files.writeString(slotLockPath, jsonObject.toString());
                } catch (Exception exception) {
                    System.getLogger("DarknessUtils").log(System.Logger.Level.INFO,
                            "Failed to update slotlock file", exception);
                }
                isSaveDirty = false;
            }

            lastDirtyCheck = now;
        });
    }

    private static KeyMapping registerKey(String translationKey, int defaultKey) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                translationKey,
                InputConstants.Type.KEYBOARD,
                defaultKey,
                KEY_CATEGORY
        ));
    }

    private static int openConfig(Minecraft client) {
        client.gui.setScreen(DarknessConfigScreen.create(client.gui.screen()));
        return 1;
    }
}
