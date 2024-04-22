package net.derfruhling.minecraft.create.trainperspective.forge;

import dev.architectury.platform.forge.EventBuses;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

@Mod(CreateTrainPerspectiveMod.MODID)
public class ModForgeEntrypoint {
    public CreateTrainPerspectiveMod mod = new CreateTrainPerspectiveMod();

    public ModForgeEntrypoint() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientSetupEvent);
    }

    private void onClientSetupEvent(FMLClientSetupEvent event) {
        EventBuses.registerModEventBus(CreateTrainPerspectiveMod.MODID, FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.addListener(this::onEntityMountEvent);
    }

    @OnlyIn(Dist.CLIENT)
    private void onEntityMountEvent(@NotNull EntityMountEvent event) {
        mod.onEntityMount(
                event.isMounting(),
                event.getEntityMounting(),
                event.getEntityBeingMounted()
        );
    }
}
