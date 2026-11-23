package me.ders.darknessutils.config;

import me.ders.darknessutils.DarknessUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DarknessConfigScreen {
    private DarknessConfigScreen() {}

    public static Screen create(Screen parent) {
        DarknessModSettings config = DarknessUtils.CONFIG;
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.darknessutils.title"))
                .setSavingRunnable(config::save);
        ConfigEntryBuilder entries = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.darknessutils.category.general"));
        addBoolean(entries, general, "slot_locking", config.doSlotLocking, true, value -> config.doSlotLocking = value);
        addBoolean(entries, general, "lock_sounds", config.playLockSounds, true, value -> config.playLockSounds = value);
        addBoolean(entries, general, "boss_highlights", config.showBossHighlights, true, value -> config.showBossHighlights = value);

        ConfigCategory automation = builder.getOrCreateCategory(Component.translatable("config.darknessutils.category.automation"));
        addBoolean(entries, automation, "auto_fix", config.autoFixEnabled, false, value -> config.autoFixEnabled = value);
        automation.addEntry(entries.startIntSlider(label("auto_fix_threshold"), config.autoFixThresholdPercent, 1, 100)
                .setDefaultValue(50).setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.autoFixThresholdPercent = value).build());
        automation.addEntry(entries.startIntSlider(label("auto_fix_cooldown"), config.autoFixCooldownSeconds, 1, 60)
                .setDefaultValue(3).setTextGetter(value -> Component.literal(value + "s"))
                .setSaveConsumer(value -> config.autoFixCooldownSeconds = value).build());
        addBoolean(entries, automation, "auto_heal", config.autoHealEnabled, false, value -> config.autoHealEnabled = value);
        automation.addEntry(entries.startIntSlider(label("auto_heal_threshold"), config.autoHealThresholdPercent, 1, 100)
                .setDefaultValue(40).setTextGetter(value -> Component.literal(value + "%"))
                .setSaveConsumer(value -> config.autoHealThresholdPercent = value).build());
        automation.addEntry(entries.startIntSlider(label("auto_heal_cooldown"), config.autoHealCooldownSeconds, 1, 60)
                .setDefaultValue(3).setTextGetter(value -> Component.literal(value + "s"))
                .setSaveConsumer(value -> config.autoHealCooldownSeconds = value).build());
        addBoolean(entries, automation, "auto_fly", config.autoFlyEnabled, false, value -> config.autoFlyEnabled = value);

        addBoss(entries, builder, "skeleton_king", config.skeletonKingOptions);
        addBoss(entries, builder, "zombie_king", config.zombieKingOptions);
        addBoss(entries, builder, "nether_king", config.netherKingOptions);
        addBoss(entries, builder, "spider_king", config.spiderKingOptions);
        addBoss(entries, builder, "dragon_slayer", config.dragonSlayerOptions);
        addBoss(entries, builder, "panda_king", config.pandaKingOptions);
        addBoss(entries, builder, "pumpkin_king", config.pumpkinKingOptions);
        addBoss(entries, builder, "ocean_prince", config.oceanPrinceOptions);
        addBoss(entries, builder, "witch_queen", config.witchQueenOptions);
        addBoss(entries, builder, "dragon_king", config.dragonKingOptions);
        addBoss(entries, builder, "super_warden", config.superWardenOptions);
        addBoss(entries, builder, "cursed_autism_father", config.cursedAFOptions);
        addBoss(entries, builder, "cursed_zeddy1977", config.cursedZEDDY1977Options);
        addBoss(entries, builder, "cursed_crankles", config.cursedCranklesOptions);
        addBoss(entries, builder, "cursed_solphire", config.cursedSolphireOptions);
        addBoss(entries, builder, "cursed_fun_mummy", config.cursedFunMummyOptions);
        addBoss(entries, builder, "cursed_skylord_gamertag", config.cursedSkylordGamertagOptions);

        ConfigCategory appearance = builder.getOrCreateCategory(Component.translatable("config.darknessutils.category.appearance"));
        appearance.addEntry(entries.startColorField(label("primary_color"), config.modColors.primary)
                .setDefaultValue(0x8955F2).setSaveConsumer(value -> config.modColors.primary = value).build());
        appearance.addEntry(entries.startColorField(label("secondary_color"), config.modColors.secondary)
                .setDefaultValue(0xD68EF9).setSaveConsumer(value -> config.modColors.secondary = value).build());
        appearance.addEntry(entries.startColorField(label("tertiary_color"), config.modColors.tertiary)
                .setDefaultValue(0xC494F9).setSaveConsumer(value -> config.modColors.tertiary = value).build());
        appearance.addEntry(entries.startColorField(label("quaternary_color"), config.modColors.quaternary)
                .setDefaultValue(0xD8C2FB).setSaveConsumer(value -> config.modColors.quaternary = value).build());

        return builder.build();
    }

    private static void addBoss(ConfigEntryBuilder entries, ConfigBuilder builder, String id, BossOptions.Entry option) {
        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.darknessutils.boss." + id));
        addBoolean(entries, category, "highlight_enabled", option.isEnabled, true, value -> option.isEnabled = value);
        category.addEntry(entries.startColorField(label("highlight_color"), option.color)
                .setDefaultValue(option.color).setSaveConsumer(value -> option.color = value).build());
    }

    private static void addBoolean(ConfigEntryBuilder entries, ConfigCategory category, String id,
                                   boolean value, boolean defaultValue, java.util.function.Consumer<Boolean> save) {
        category.addEntry(entries.startBooleanToggle(label(id), value)
                .setDefaultValue(defaultValue).setSaveConsumer(save).build());
    }

    private static Component label(String id) {
        return Component.translatable("config.darknessutils.option." + id);
    }
}
