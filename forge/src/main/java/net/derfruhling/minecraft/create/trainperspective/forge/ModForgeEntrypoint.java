package net.derfruhling.minecraft.create.trainperspective.forge;

import dev.architectury.platform.forge.EventBuses;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.ModConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateTrainPerspectiveMod.MODID)
public class ModForgeEntrypoint {
    public CreateTrainPerspectiveMod mod = new CreateTrainPerspectiveMod();

    public ModForgeEntrypoint() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientSetupEvent);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> ModConfig.createConfigScreen(screen)));
    }

    private void onClientSetupEvent(FMLClientSetupEvent event) {
        EventBuses.registerModEventBus(CreateTrainPerspectiveMod.MODID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
