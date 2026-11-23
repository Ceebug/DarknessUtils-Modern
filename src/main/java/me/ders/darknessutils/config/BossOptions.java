package me.ders.darknessutils.config;

public final class BossOptions {
    private BossOptions() {}

    public static class Entry {
        public int color;
        public boolean isEnabled = true;
        protected Entry(int color) { this.color = color; }
        public int color() { return color; }
        public boolean isEnabled() { return isEnabled; }
    }

    public static class SkeletonKingOptions extends Entry { public SkeletonKingOptions() { super(0xCCCCCC); } }
    public static class ZombieKingOptions extends Entry { public ZombieKingOptions() { super(0x38761D); } }
    public static class NetherKingOptions extends Entry { public NetherKingOptions() { super(0xB45F06); } }
    public static class SpiderKingOptions extends Entry { public SpiderKingOptions() { super(0x45D6E8); } }
    public static class DragonSlayerOptions extends Entry { public DragonSlayerOptions() { super(0xE95BFF); } }
    public static class PandaKingOptions extends Entry { public PandaKingOptions() { super(0x6AA84F); } }
    public static class PumpkinKingOptions extends Entry { public PumpkinKingOptions() { super(0xB44409); } }
    public static class OceanPrinceOptions extends Entry { public OceanPrinceOptions() { super(0x3D85C6); } }
    public static class WitchQueenOptions extends Entry { public WitchQueenOptions() { super(0x674EA7); } }
    public static class DragonKingOptions extends Entry { public DragonKingOptions() { super(0x1B0F29); } }
    public static class SuperWardenOptions extends Entry { public SuperWardenOptions() { super(0x0B5394); } }
    public static class CursedAFOptions extends Entry { public CursedAFOptions() { super(0xFF0014); } }
    public static class CursedZEDDY1977Options extends Entry { public CursedZEDDY1977Options() { super(0xFF0014); } }
    public static class CursedCrankles_Options extends Entry { public CursedCrankles_Options() { super(0xFF0014); } }
    public static class CursedSolphireOptions extends Entry { public CursedSolphireOptions() { super(0xFF0014); } }
    public static class CursedFunMummyOptions extends Entry { public CursedFunMummyOptions() { super(0xFF0014); } }
    public static class CursedSkylordGamertagOptions extends Entry { public CursedSkylordGamertagOptions() { super(0xFF0014); } }
}
