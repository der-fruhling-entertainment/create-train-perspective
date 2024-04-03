package net.derfruhling.minecraft.create.trainperspective.forge;

import dev.architectury.platform.forge.EventBuses;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

@Mod(CreateTrainPerspectiveMod.MODID)
public class ModForgeEntrypoint {
    public CreateTrainPerspectiveMod mod = new CreateTrainPerspectiveMod();

    public ModForgeEntrypoint() {
        EventBuses.registerModEventBus(CreateTrainPerspectiveMod.MODID, FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.addListener(this::onEntityMountEvent);
    }

    private void onEntityMountEvent(@NotNull EntityMountEvent event) {
        mod.onEntityMount(
                event.isMounting(),
                event.getEntityMounting(),
                event.getEntityBeingMounted()
        );
    }
}
