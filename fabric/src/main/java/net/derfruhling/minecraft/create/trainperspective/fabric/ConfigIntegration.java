package net.derfruhling.minecraft.create.trainperspective.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.derfruhling.minecraft.create.trainperspective.ModConfig;

public class ConfigIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfig::createConfigScreen;
    }
}