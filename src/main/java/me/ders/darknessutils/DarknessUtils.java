package me.ders.darknessutils;

import me.ders.darknessutils.config.DarknessModSettings;
import me.ders.darknessutils.features.AutoFix;
import me.ders.darknessutils.features.AutoHeal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;

public class DarknessUtils implements ModInitializer {
    public static final DarknessModSettings CONFIG = DarknessModSettings.createAndLoad();

    public static AutoFix autoFix;
    public static AutoHeal autoHeal;

    public static KeyBinding lockBinding;
    public static KeyBinding fixBinding;
    public static KeyBinding healBinding;

    public static boolean isSaveDirty = false;
    private long lastDirtyCheck = System.currentTimeMillis();

    public static LinkedHashSet<Integer> lockedSlots = new LinkedHashSet<>();

    @Override
    public void onInitialize() {
        autoFix = AutoFix.getInstance();
        autoHeal = AutoHeal.getInstance();

        lockBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.slotlock",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_LEFT_ALT,
                "key.categories.inventory"
        ));

        fixBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fix",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_F,
                "key.categories.gameplay"
        ));

        healBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.heal",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_H,
                "key.categories.gameplay"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long currentDirtyCheck = System.currentTimeMillis();
            if (currentDirtyCheck - lastDirtyCheck > 2000) {
                if (isSaveDirty) {
                    File slotLockFile = new File(MinecraftClient.getInstance().runDirectory, "slotlock.json");
                    Path slotLockPath = Paths.get(slotLockFile.getAbsolutePath());
                    String json = "{ }";
                    try {
                        json = Files.readString(slotLockPath);
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
                    } catch (Exception e) {
                        System.getLogger("DarknessUtils").log(System.Logger.Level.INFO,
                                "Failed to update slotlock file", e);
                    }
                    isSaveDirty = false;
                }
                lastDirtyCheck = currentDirtyCheck;
            }
        });
    }
}
