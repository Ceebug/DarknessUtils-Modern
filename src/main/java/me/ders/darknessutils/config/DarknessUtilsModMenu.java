package me.ders.darknessutils.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

@Environment(EnvType.CLIENT)
public final class DarknessUtilsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            System.getLogger("DarknessUtils").log(System.Logger.Level.WARNING,
                    "Mod Menu config button unavailable: Cloth Config is not installed.");
            return null;
        }
        return parent -> {
            try {
                return DarknessConfigScreen.create(parent);
            } catch (Throwable exception) {
                System.getLogger("DarknessUtils").log(System.Logger.Level.ERROR,
                        "Mod Menu could not create the DarknessUtils configuration screen.", exception);
                throw exception;
            }
        };
    }
}
