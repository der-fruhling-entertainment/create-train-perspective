package net.derfruhling.minecraft.create.trainperspective;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateTrainPerspectiveMod.MODID)
public class CreateTrainPerspectiveMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_train_perspective";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateTrainPerspectiveMod() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static class RotationState {
        private final CarriageContraptionEntity entity;
        private float lastYaw, lastPitch;

        public RotationState(CarriageContraptionEntity entity) {
            this.entity = entity;
            lastYaw = entity.yaw;
        }

        public float getYawDelta() {
            var rotation = entity.yaw - lastYaw;
            lastYaw = entity.yaw;
            return rotation;
        }

        public float getPitchDelta() {
            var rotation = entity.pitch - lastPitch;
            lastPitch = entity.pitch;
            return rotation;
        }
    }

    private final HashMap<UUID, RotationState> states = new HashMap<>();

    @SubscribeEvent
    public void onEntityMount(final EntityMountEvent event) {
        if(
                event.getEntityMounting() instanceof LocalPlayer &&
                event.getEntityBeingMounted() instanceof CarriageContraptionEntity
        ) {
            if(event.isMounting()) {
                LOGGER.info("PLAYER MOUNT {} -> {}", event.getEntityMounting(), event.getEntityBeingMounted());
                states.put(event.getEntityMounting().getUUID(), new RotationState((CarriageContraptionEntity) event.getEntityBeingMounted()));
            } else {
                LOGGER.info("PLAYER DISMOUNT {} -> {}", event.getEntityMounting(), event.getEntityBeingMounted());
                states.remove(event.getEntityMounting().getUUID());
            }
        } else {
            LOGGER.info("Mount {} -> {}", event.getEntityMounting(), event.getEntityBeingMounted());
        }
    }

    @SubscribeEvent
    public void onTickPlayer(final TickEvent.PlayerTickEvent event) {
        if(event.side != LogicalSide.CLIENT) return;
        if(states.containsKey(event.player.getUUID()) && event.player.isLocalPlayer()) {
            var state = states.get(event.player.getUUID());
            // TODO vertical rotation disabled for now
            //      its broken
            /*var xRot = event.player.getXRot();*/
            var yRot = event.player.getYRot();

            /*while(xRot < 0.0f) xRot += 360.0f;*/
            while(yRot < 0.0f) yRot += 360.0f;

            /*var xDelta = state.getPitchDelta();*/
            var yDelta = state.getYawDelta();

            /*if(xDelta != 0.0f) {
                LOGGER.info("{}", yRot);
                event.player.setXRot((xRot - xDelta) % 360.0f);
            }*/
            if(yDelta != 0.0f) event.player.setYRot(yRot + yDelta);
        }
    }
}
