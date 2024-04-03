package net.derfruhling.minecraft.create.trainperspective.fabric;

import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.fabricmc.api.ClientModInitializer;

public class ModFabricEntrypoint implements ClientModInitializer {
    private static ModFabricEntrypoint INSTANCE;

    public CreateTrainPerspectiveMod common;

    @Override
    public void onInitializeClient() {
        common = new CreateTrainPerspectiveMod();
        INSTANCE = this;
    }

    public static ModFabricEntrypoint getInstance() {
        return INSTANCE;
    }
}
