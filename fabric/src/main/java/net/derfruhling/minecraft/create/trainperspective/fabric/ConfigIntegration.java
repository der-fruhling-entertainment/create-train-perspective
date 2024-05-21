package net.derfruhling.minecraft.create.trainperspective.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.derfruhling.minecraft.create.trainperspective.ModConfig;
import net.minecraft.client.gui.screens.Screen;

public class ConfigIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfig::createConfigScreen;
    }
}