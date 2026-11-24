package me.ders.darknessutils.features;

import me.ders.darknessutils.DarknessUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public final class WarningSound {
    private WarningSound() {}

    public static void play(Player player) {
        if (!DarknessUtils.CONFIG.warningSoundEnabled) {
            return;
        }

        try {
            Identifier soundId = Identifier.parse(DarknessUtils.CONFIG.warningSound);
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
            if (sound != null) {
                player.playSound(sound, 1.0F, 1.0F);
            }
        } catch (IllegalArgumentException exception) {
            System.getLogger("DarknessUtils").log(System.Logger.Level.WARNING,
                    "Invalid warning sound ID: " + DarknessUtils.CONFIG.warningSound, exception);
        }
    }
}
