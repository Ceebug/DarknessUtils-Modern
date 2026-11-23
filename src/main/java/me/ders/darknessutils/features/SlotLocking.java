package me.ders.darknessutils.features;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.ders.darknessutils.DarknessUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;

public final class SlotLocking {
    private static final Gson GSON = new Gson();

    private SlotLocking() {}

    public static LinkedHashSet<Integer> getLockedSlots() {
        return new LinkedHashSet<>(DarknessUtils.lockedSlots);
    }

    public static boolean isLocked(int slot) {
        return DarknessUtils.CONFIG.doSlotLocking() && DarknessUtils.lockedSlots.contains(slot);
    }

    public static void lockSlot(int slot) {
        if (slot < 0 || slot >= 41 || !DarknessUtils.lockedSlots.add(slot)) return;
        playLockSound(1.0F);
        DarknessUtils.isSaveDirty = true;
    }

    public static void unlockSlot(int slot) {
        if (!DarknessUtils.lockedSlots.remove(slot)) return;
        playLockSound(0.5F);
        DarknessUtils.isSaveDirty = true;
    }

    private static void playLockSound(float pitch) {
        Minecraft client = Minecraft.getInstance();
        if (DarknessUtils.CONFIG.playLockSounds() && client.player != null) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 0.5F, pitch);
        }
    }

    public static void handleJoinWorld() {
        DarknessUtils.lockedSlots.clear();
        Path file = FabricLoader.getInstance().getGameDir().resolve("slotlock.json");
        try {
            if (Files.notExists(file)) Files.writeString(file, "{ }");
            JsonObject root = GSON.fromJson(Files.readString(file), JsonObject.class);
            if (root == null) return;
            JsonArray values = root.getAsJsonArray("lockedSlots");
            if (values == null) return;
            values.forEach(value -> {
                try {
                    int slot = value.getAsInt();
                    if (slot >= 0 && slot < 41) DarknessUtils.lockedSlots.add(slot);
                } catch (RuntimeException ignored) {
                }
            });
        } catch (Exception exception) {
            System.getLogger("DarknessUtils").log(System.Logger.Level.WARNING,
                    "Unable to load locked slots", exception);
        }
    }
}
