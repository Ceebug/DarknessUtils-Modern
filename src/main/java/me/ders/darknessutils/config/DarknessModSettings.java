package me.ders.darknessutils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DarknessModSettings {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("darknessutils.json");

    public boolean doSlotLocking = true;
    public boolean playLockSounds = true;
    public boolean showBossHighlights = true;

    public boolean autoFixEnabled = false;
    public int autoFixThresholdPercent = 50;
    public int autoFixCooldownSeconds = 3;
    public boolean autoHealEnabled = false;
    public int autoHealThresholdPercent = 40;
    public int autoHealCooldownSeconds = 3;
    public boolean autoFlyEnabled = false;

    public BossOptions.SkeletonKingOptions skeletonKingOptions = new BossOptions.SkeletonKingOptions();
    public BossOptions.ZombieKingOptions zombieKingOptions = new BossOptions.ZombieKingOptions();
    public BossOptions.NetherKingOptions netherKingOptions = new BossOptions.NetherKingOptions();
    public BossOptions.SpiderKingOptions spiderKingOptions = new BossOptions.SpiderKingOptions();
    public BossOptions.DragonSlayerOptions dragonSlayerOptions = new BossOptions.DragonSlayerOptions();
    public BossOptions.PandaKingOptions pandaKingOptions = new BossOptions.PandaKingOptions();
    public BossOptions.PumpkinKingOptions pumpkinKingOptions = new BossOptions.PumpkinKingOptions();
    public BossOptions.OceanPrinceOptions oceanPrinceOptions = new BossOptions.OceanPrinceOptions();
    public BossOptions.WitchQueenOptions witchQueenOptions = new BossOptions.WitchQueenOptions();
    public BossOptions.DragonKingOptions dragonKingOptions = new BossOptions.DragonKingOptions();
    public BossOptions.SuperWardenOptions superWardenOptions = new BossOptions.SuperWardenOptions();
    public BossOptions.CursedAFOptions cursedAFOptions = new BossOptions.CursedAFOptions();
    public BossOptions.CursedZEDDY1977Options cursedZEDDY1977Options = new BossOptions.CursedZEDDY1977Options();
    public BossOptions.CursedCrankles_Options cursedCranklesOptions = new BossOptions.CursedCrankles_Options();
    public BossOptions.CursedSolphireOptions cursedSolphireOptions = new BossOptions.CursedSolphireOptions();
    public BossOptions.CursedFunMummyOptions cursedFunMummyOptions = new BossOptions.CursedFunMummyOptions();
    public BossOptions.CursedSkylordGamertagOptions cursedSkylordGamertagOptions = new BossOptions.CursedSkylordGamertagOptions();

    public ModColors modColors = new ModColors();

    public static DarknessModSettings createAndLoad() {
        if (Files.notExists(CONFIG_PATH)) {
            DarknessModSettings defaults = new DarknessModSettings();
            defaults.save();
            return defaults;
        }
        try {
            DarknessModSettings loaded = GSON.fromJson(Files.readString(CONFIG_PATH), DarknessModSettings.class);
            return loaded == null ? new DarknessModSettings() : loaded;
        } catch (IOException | RuntimeException exception) {
            System.getLogger("DarknessUtils").log(System.Logger.Level.WARNING,
                    "Could not load darknessutils.json; using default settings.", exception);
            return new DarknessModSettings();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException exception) {
            System.getLogger("DarknessUtils").log(System.Logger.Level.ERROR,
                    "Could not save darknessutils.json.", exception);
        }
    }

    public boolean doSlotLocking() { return doSlotLocking; }
    public boolean playLockSounds() { return playLockSounds; }
    public boolean showBossHighlights() { return showBossHighlights; }

    public static class ModColors {
        public int primary = 0x8955F2;
        public int secondary = 0xD68EF9;
        public int tertiary = 0xC494F9;
        public int quaternary = 0xD8C2FB;
        public int primary() { return primary; }
        public int secondary() { return secondary; }
        public int tertiary() { return tertiary; }
        public int quaternary() { return quaternary; }
    }
}
