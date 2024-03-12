package net.derfruhling.minecraft.create.trainperspective;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
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

        MinecraftForge.EVENT_BUS.addListener(this::onEntityMount);
        MinecraftForge.EVENT_BUS.addListener(this::onTickPlayer);
    }

    private static class RotationState {
        public final CarriageContraptionEntity entity;
        private float lastYaw;

        public RotationState(CarriageContraptionEntity entity) {
            this.entity = entity;
            lastYaw = entity.yaw;
        }

        public float getYawDelta() {
            var rotation = entity.yaw - lastYaw;
            lastYaw = entity.yaw;
            return rotation;
        }
    }

    private final HashMap<UUID, RotationState> states = new HashMap<>();

    public void onEntityMount(final EntityMountEvent event) {
        if(
                event.getEntityMounting() instanceof AbstractClientPlayer &&
                        event.getEntityBeingMounted() instanceof CarriageContraptionEntity
        ) {
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((AbstractClientPlayer) event.getEntityMounting());
            if(event.isMounting()) {
                var state = new RotationState((CarriageContraptionEntity) event.getEntityBeingMounted());
                states.put(event.getEntityMounting().getUUID(), state);
                persp.enable(state.entity.pitch);
            } else {
                states.remove(event.getEntityMounting().getUUID());
                persp.disable();
            }
        }
    }

    public void onTickPlayer(final TickEvent.PlayerTickEvent event) {
        if(event.side != LogicalSide.CLIENT) return;
        if(states.containsKey(event.player.getUUID()) && event.player.isLocalPlayer() && event.player instanceof AbstractClientPlayer) {
            var state = states.get(event.player.getUUID());
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((AbstractClientPlayer) event.player);
            persp.setLean(state.entity.pitch);
            event.player.setYRot(event.player.getYRot() + state.getYawDelta());
        }
    }
}
