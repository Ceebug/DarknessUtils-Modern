package me.ders.darknessutils.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import me.ders.darknessutils.DarknessUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class DarknessConfigScreen {
    private DarknessConfigScreen() {}

    public static Screen create(Screen parent) {
        DarknessModSettings config = DarknessUtils.CONFIG;
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.darknessutils.title"))
                .save(config::save);

        ConfigCategory.Builder general = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.category.general"));
        general.option(boolOption("slot_locking", config.doSlotLocking,
                config::doSlotLocking, value -> config.doSlotLocking = value));
        general.option(boolOption("lock_sounds", config.playLockSounds,
                config::playLockSounds, value -> config.playLockSounds = value));
        general.option(boolOption("boss_highlights", config.showBossHighlights,
                config::showBossHighlights, value -> config.showBossHighlights = value));
        builder.category(general.build());

        ConfigCategory.Builder automation = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.category.automation"));
        automation.option(boolOption("auto_fix", false,
                () -> config.autoFixEnabled, value -> config.autoFixEnabled = value));
        automation.option(sliderOption("auto_fix_threshold", 50, 1, 100,
                () -> config.autoFixThresholdPercent, value -> config.autoFixThresholdPercent = value));
        automation.option(sliderOption("auto_fix_cooldown", 3, 1, 60,
                () -> config.autoFixCooldownSeconds, value -> config.autoFixCooldownSeconds = value));
        automation.option(boolOption("auto_heal", false,
                () -> config.autoHealEnabled, value -> config.autoHealEnabled = value));
        automation.option(sliderOption("auto_heal_threshold", 40, 1, 100,
                () -> config.autoHealThresholdPercent, value -> config.autoHealThresholdPercent = value));
        automation.option(sliderOption("auto_heal_cooldown", 3, 1, 60,
                () -> config.autoHealCooldownSeconds, value -> config.autoHealCooldownSeconds = value));
        automation.option(boolOption("auto_fly", false,
                () -> config.autoFlyEnabled, value -> config.autoFlyEnabled = value));
        builder.category(automation.build());

        builder.category(bossCategory("skeleton_king", config.skeletonKingOptions));
        builder.category(bossCategory("zombie_king", config.zombieKingOptions));
        builder.category(bossCategory("nether_king", config.netherKingOptions));
        builder.category(bossCategory("spider_king", config.spiderKingOptions));
        builder.category(bossCategory("dragon_slayer", config.dragonSlayerOptions));
        builder.category(bossCategory("panda_king", config.pandaKingOptions));
        builder.category(bossCategory("pumpkin_king", config.pumpkinKingOptions));
        builder.category(bossCategory("ocean_prince", config.oceanPrinceOptions));
        builder.category(bossCategory("witch_queen", config.witchQueenOptions));
        builder.category(bossCategory("dragon_king", config.dragonKingOptions));
        builder.category(bossCategory("super_warden", config.superWardenOptions));
        builder.category(bossCategory("cursed_autism_father", config.cursedAFOptions));
        builder.category(bossCategory("cursed_zeddy1977", config.cursedZEDDY1977Options));
        builder.category(bossCategory("cursed_crankles", config.cursedCranklesOptions));
        builder.category(bossCategory("cursed_solphire", config.cursedSolphireOptions));
        builder.category(bossCategory("cursed_fun_mummy", config.cursedFunMummyOptions));
        builder.category(bossCategory("cursed_skylord_gamertag", config.cursedSkylordGamertagOptions));
        builder.category(bossCategory("tiny_terror", config.tinyTerrorOptions));
        builder.category(bossCategory("parched_king", config.parchedKingOptions));
        builder.category(bossCategory("frost_stray", config.frostStrayOptions));

        ConfigCategory.Builder appearance = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.category.appearance"));
        appearance.option(colorOption("primary_color", 0x8955F2,
                () -> config.modColors.primary, value -> config.modColors.primary = value));
        appearance.option(colorOption("secondary_color", 0xD68EF9,
                () -> config.modColors.secondary, value -> config.modColors.secondary = value));
        appearance.option(colorOption("tertiary_color", 0xC494F9,
                () -> config.modColors.tertiary, value -> config.modColors.tertiary = value));
        appearance.option(colorOption("quaternary_color", 0xD8C2FB,
                () -> config.modColors.quaternary, value -> config.modColors.quaternary = value));
        builder.category(appearance.build());

        return builder.build().generateScreen(parent);
    }

    private static ConfigCategory bossCategory(String id, BossOptions.Entry options) {
        ConfigCategory.Builder category = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.boss." + id));
        category.option(boolOption("highlight_enabled", true,
                () -> options.isEnabled, value -> options.isEnabled = value));
        category.option(colorOption("highlight_color", options.color,
                () -> options.color, value -> options.color = value));
        return category.build();
    }

    private static Option<Boolean> boolOption(String id, boolean defaultValue,
                                               Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(label(id))
                .binding(defaultValue, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Integer> sliderOption(String id, int defaultValue, int min, int max,
                                                 Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(label(id))
                .binding(defaultValue, getter, setter)
                .controller(option -> IntegerSliderControllerBuilder.create(option).range(min, max).step(1))
                .build();
    }

    private static Option<Color> colorOption(String id, int defaultValue,
                                              Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Color>createBuilder()
                .name(label(id))
                .binding(new Color(defaultValue), () -> new Color(getter()), color -> setter.accept(color.getRGB() & 0xFFFFFF))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                .build();
    }

    private static Component label(String id) {
        return Component.translatable("config.darknessutils.option." + id);
    }
}
