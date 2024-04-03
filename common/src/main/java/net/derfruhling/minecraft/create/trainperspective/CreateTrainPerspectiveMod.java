package net.derfruhling.minecraft.create.trainperspective;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
public class CreateTrainPerspectiveMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_train_perspective";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateTrainPerspectiveMod() {
        TickEvent.PLAYER_POST.register(this::onTickPlayer);
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

    public void onEntityMount(boolean isMounting, Entity entityMounting, Entity entityBeingMounted) {
        if(
                entityMounting instanceof LocalPlayer player &&
                entityBeingMounted instanceof CarriageContraptionEntity contraption
        ) {
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if(isMounting) {
                var state = new RotationState(contraption);
                states.put(entityMounting.getUUID(), state);
                persp.enable(state.entity.pitch, state.entity.yaw);
            } else {
                states.remove(entityMounting.getUUID());
                persp.disable();
            }
        }
    }

    public void onTickPlayer(final Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;
        if(states.containsKey(player.getUUID())) {
            var state = states.get(player.getUUID());
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((AbstractClientPlayer) player);
            persp.setLean(state.entity.pitch);
            persp.setYaw(state.entity.yaw);
            player.setYRot(player.getYRot() + state.getYawDelta());
        }
    }
}
