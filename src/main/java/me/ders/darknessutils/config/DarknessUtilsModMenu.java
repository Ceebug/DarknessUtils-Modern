package me.ders.darknessutils.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class DarknessUtilsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Always register a config button. If Cloth Config is missing, explain the dependency
        // instead of returning null (which makes Mod Menu hide the button entirely).
        return parent -> {
            if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
                return new ConfirmScreen(
                        accepted -> Minecraft.getInstance().gui.setScreen(parent),
                        Component.translatable("message.darknessutils.config.cloth_config_required.title"),
                        Component.translatable("message.darknessutils.config.cloth_config_required.message")
                );
            }
            return DarknessConfigScreen.create(parent);
        };
    }
}
