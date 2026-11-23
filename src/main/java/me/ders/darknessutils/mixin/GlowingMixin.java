package me.ders.darknessutils.mixin;

import me.ders.darknessutils.DarknessUtils;
import me.ders.darknessutils.config.BossOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class GlowingMixin {
    @Shadow public abstract World getWorld();
    @Shadow protected abstract boolean getFlag(int index);
    @Shadow @Final protected static int GLOWING_FLAG_INDEX;
    @Shadow private boolean glowing;
    @Shadow public abstract Text getCustomName();
    @Shadow public abstract boolean hasCustomName();
    @Shadow private World world;

    private final Map<String, Supplier<BossOptions.Entry>> mobColors = new HashMap<>() {{
        put("Skeleton King", () -> DarknessUtils.CONFIG.skeletonKingOptions);
        put("Zombie King", () -> DarknessUtils.CONFIG.zombieKingOptions);
        put("Nether King", () -> DarknessUtils.CONFIG.netherKingOptions);
        put("Spider King", () -> DarknessUtils.CONFIG.spiderKingOptions);
        put("Dragon Slayer", () -> DarknessUtils.CONFIG.dragonSlayerOptions);
        put("Panda King", () -> DarknessUtils.CONFIG.pandaKingOptions);
        put("Pumpkin King", () -> DarknessUtils.CONFIG.pumpkinKingOptions);
        put("Ocean Prince", () -> DarknessUtils.CONFIG.oceanPrinceOptions);
        put("Witch Queen", () -> DarknessUtils.CONFIG.witchQueenOptions);
        put("Dragon King", () -> DarknessUtils.CONFIG.dragonKingOptions);
        put("Super Warden", () -> DarknessUtils.CONFIG.superWardenOptions);
        put("Cursed AutismFather", () -> DarknessUtils.CONFIG.cursedAFOptions);
        put("Cursed ZEDDY1977", () -> DarknessUtils.CONFIG.cursedZEDDY1977Options);
        put("Cursed Crankles_", () -> DarknessUtils.CONFIG.cursedCranklesOptions);
        put("Cursed Solphire", () -> DarknessUtils.CONFIG.cursedSolphireOptions);
        put("Cursed FunMummy", () -> DarknessUtils.CONFIG.cursedFunMummyOptions);
    }};

    /**
     * @author Der_s
     * @reason Calls to Entity#setGlowing do not work for client sided entities.
     */
    @Overwrite
    public boolean isGlowing() {
        if (hasCustomName()) {
            Optional<Map.Entry<String, Supplier<BossOptions.Entry>>> mobColor = getMobColor();
            if (mobColor.isPresent()
                    && (world.getBlockState(new BlockPos(95, 3, -251)).getBlock().asItem() == Items.BEDROCK || world.isClient())) {
                return mobColor.get().getValue().get().isEnabled() && DarknessUtils.CONFIG.showBossHighlights();
            }
        }
        return getWorld().isClient() ? getFlag(GLOWING_FLAG_INDEX) : glowing;
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    public void injectGetTeamColorValue(CallbackInfoReturnable<Integer> ci) {
        if (hasCustomName() && DarknessUtils.CONFIG.showBossHighlights()) {
            getMobColor().ifPresent(entry -> ci.setReturnValue(entry.getValue().get().color()));
        }
    }

    public Optional<Map.Entry<String, Supplier<BossOptions.Entry>>> getMobColor() {
        if (!hasCustomName()) return Optional.empty();
        String name = getCustomName().getString().toLowerCase();
        return mobColors.entrySet().stream()
                .filter(entry -> name.contains(entry.getKey().toLowerCase()))
                .findFirst();
    }
}
