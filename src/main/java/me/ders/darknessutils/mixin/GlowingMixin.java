package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import me.ders.darknessutils.config.BossOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(Entity.class)
public abstract class GlowingMixin {
    @Shadow public abstract boolean hasCustomName();
    @Shadow public abstract Component getCustomName();

    private static final Map<String, Supplier<BossOptions.Entry>> DARKNESSUTILS_BOSS_COLORS = new LinkedHashMap<>();
    static {
        DARKNESSUTILS_BOSS_COLORS.put("Skeleton King", () -> DarknessUtils.CONFIG.skeletonKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Zombie King", () -> DarknessUtils.CONFIG.zombieKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Nether King", () -> DarknessUtils.CONFIG.netherKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Spider King", () -> DarknessUtils.CONFIG.spiderKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Dragon Slayer", () -> DarknessUtils.CONFIG.dragonSlayerOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Panda King", () -> DarknessUtils.CONFIG.pandaKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Pumpkin King", () -> DarknessUtils.CONFIG.pumpkinKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Ocean Prince", () -> DarknessUtils.CONFIG.oceanPrinceOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Witch Queen", () -> DarknessUtils.CONFIG.witchQueenOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Dragon King", () -> DarknessUtils.CONFIG.dragonKingOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Super Warden", () -> DarknessUtils.CONFIG.superWardenOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Cursed AutismFather", () -> DarknessUtils.CONFIG.cursedAFOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Cursed ZEDDY1977", () -> DarknessUtils.CONFIG.cursedZEDDY1977Options);
        DARKNESSUTILS_BOSS_COLORS.put("Cursed Crankles_", () -> DarknessUtils.CONFIG.cursedCranklesOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Cursed Solphire", () -> DarknessUtils.CONFIG.cursedSolphireOptions);
        DARKNESSUTILS_BOSS_COLORS.put("Cursed FunMummy", () -> DarknessUtils.CONFIG.cursedFunMummyOptions);
    }

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void darknessutils$enableNamedBossGlow(CallbackInfoReturnable<Boolean> cir) {
        if (!DarknessUtils.CONFIG.showBossHighlights() || !hasCustomName()) return;
        Optional<BossOptions.Entry> match = getBossOptions();
        if (match.isPresent() && ((Entity) (Object) this).level().isClientSide()) {
            cir.setReturnValue(match.get().isEnabled());
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void darknessutils$setNamedBossGlowColor(CallbackInfoReturnable<Integer> cir) {
        if (DarknessUtils.CONFIG.showBossHighlights() && hasCustomName()) {
            getBossOptions().ifPresent(option -> cir.setReturnValue(option.color()));
        }
    }

    private Optional<BossOptions.Entry> getBossOptions() {
        String name = getCustomName().getString().toLowerCase();
        return DARKNESSUTILS_BOSS_COLORS.entrySet().stream()
                .filter(entry -> name.contains(entry.getKey().toLowerCase()))
                .map(entry -> entry.getValue().get())
                .findFirst();
    }
}
