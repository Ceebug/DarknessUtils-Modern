package me.ders.darknessutils.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
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
        builder.category(general.build());

        ConfigCategory.Builder warnings = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.category.warnings"));
        warnings.group(warningGroup("low_durability_warning", () -> config.lowDurabilityWarningEnabled,
                value -> config.lowDurabilityWarningEnabled = value,
                () -> config.lowDurabilityWarningThresholdPercent,
                value -> config.lowDurabilityWarningThresholdPercent = value, 50));
        warnings.group(warningGroup("health_warning", () -> config.healthWarningEnabled,
                value -> config.healthWarningEnabled = value,
                () -> config.healthWarningThresholdPercent,
                value -> config.healthWarningThresholdPercent = value, 40));
        builder.category(warnings.build());

        ConfigCategory.Builder highlighting = ConfigCategory.createBuilder()
                .name(Component.translatable("config.darknessutils.category.highlighting"));
        highlighting.option(boolOption("boss_highlights", config.showBossHighlights,
                config::showBossHighlights, value -> config.showBossHighlights = value));
        highlighting.group(bossGroup("skeleton_king", config.skeletonKingOptions));
        highlighting.group(bossGroup("zombie_king", config.zombieKingOptions));
        highlighting.group(bossGroup("nether_king", config.netherKingOptions));
        highlighting.group(bossGroup("spider_king", config.spiderKingOptions));
        highlighting.group(bossGroup("dragon_slayer", config.dragonSlayerOptions));
        highlighting.group(bossGroup("panda_king", config.pandaKingOptions));
        highlighting.group(bossGroup("pumpkin_king", config.pumpkinKingOptions));
        highlighting.group(bossGroup("ocean_prince", config.oceanPrinceOptions));
        highlighting.group(bossGroup("witch_queen", config.witchQueenOptions));
        highlighting.group(bossGroup("dragon_king", config.dragonKingOptions));
        highlighting.group(bossGroup("super_warden", config.superWardenOptions));
        highlighting.group(bossGroup("cursed_autism_father", config.cursedAFOptions));
        highlighting.group(bossGroup("cursed_zeddy1977", config.cursedZEDDY1977Options));
        highlighting.group(bossGroup("cursed_crankles", config.cursedCranklesOptions));
        highlighting.group(bossGroup("cursed_solphire", config.cursedSolphireOptions));
        highlighting.group(bossGroup("cursed_fun_mummy", config.cursedFunMummyOptions));
        highlighting.group(bossGroup("cursed_skylord_gamertag", config.cursedSkylordGamertagOptions));
        highlighting.group(bossGroup("tiny_terror", config.tinyTerrorOptions));
        highlighting.group(bossGroup("parched_king", config.parchedKingOptions));
        highlighting.group(bossGroup("frost_stray", config.frostStrayOptions));
        builder.category(highlighting.build());


        return builder.build().generateScreen(parent);
    }

    private static OptionGroup warningGroup(String id, Supplier<Boolean> enabledGetter, Consumer<Boolean> enabledSetter,
                                             Supplier<Integer> thresholdGetter, Consumer<Integer> thresholdSetter,
                                             int defaultThreshold) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Component.translatable("config.darknessutils.warning." + id));
        group.option(boolOption("enable_warning", true, enabledGetter, enabledSetter));
        group.option(sliderOption(id + "_threshold", defaultThreshold, 1, 100,
                thresholdGetter, thresholdSetter));
        return group.build();
    }

    private static OptionGroup bossGroup(String id, BossOptions.Entry options) {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Component.translatable("config.darknessutils.boss." + id));
        group.option(boolOption("highlight_enabled", true,
                () -> options.isEnabled, value -> options.isEnabled = value));
        group.option(colorOption("highlight_color", options.color,
                () -> options.color, value -> options.color = value));
        return group.build();
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
                .binding(new Color(defaultValue), () -> new Color(getter.get()), color -> setter.accept(color.getRGB() & 0xFFFFFF))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(false))
                .build();
    }

    private static Component label(String id) {
        return Component.translatable("config.darknessutils.option." + id);
    }
}
