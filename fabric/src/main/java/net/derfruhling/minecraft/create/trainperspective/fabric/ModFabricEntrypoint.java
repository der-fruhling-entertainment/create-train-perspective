package net.derfruhling.minecraft.create.trainperspective.fabric;

import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.fabricmc.api.ClientModInitializer;

public class ModFabricEntrypoint implements ClientModInitializer {
    private static ModFabricEntrypoint INSTANCE;

    public CreateTrainPerspectiveMod common;

    public static ModFabricEntrypoint getInstance() {
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        common = new CreateTrainPerspectiveMod();
        INSTANCE = this;
    }
}
