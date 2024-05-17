package net.derfruhling.minecraft.create.trainperspective;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
public class CreateTrainPerspectiveMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_train_perspective";
    public static CreateTrainPerspectiveMod INSTANCE;
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateTrainPerspectiveMod() {
        TickEvent.PLAYER_POST.register(this::onTickPlayer);
        INSTANCE = this;
    }

    private static class RotationState {
        public final CarriageContraptionEntity entity;
        private float lastYaw;
        public boolean standingState;
        public boolean isMounted;
        public boolean shouldTickState = true;
        public int ticksSinceLastUpdate = 0;

        public RotationState(CarriageContraptionEntity entity, boolean standingState, boolean isMounted) {
            this.entity = entity;
            lastYaw = entity.yaw;
            this.standingState = standingState;
            this.isMounted = isMounted;
        }

        public float getYawDelta() {
            while (entity.yaw - lastYaw < -180.0f) {
                lastYaw -= 360.0f;
            }

            while (entity.yaw - lastYaw >= 180.0f) {
                lastYaw += 360.0f;
            }

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
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if(isMounting) {
                if(!states.containsKey(entityMounting.getUUID())) {
                    var state = new RotationState(contraption, false, true);
                    states.put(entityMounting.getUUID(), state);
                    persp.enable(state.entity.pitch, state.entity.yaw);
                } else {
                    var state = states.get(entityMounting.getUUID());
                    state.isMounted = true;
                    state.shouldTickState = true;
                }
            } else {
                if(states.containsKey(entityMounting.getUUID())) {
                    states.remove(entityMounting.getUUID());
                    persp.disable();
                }
            }
        }
    }

    public void tickStandingPlayer(final CarriageContraptionEntity contraption, final Player player) {
        if(player.getVehicle() != null) return;

        var state = states.get(player.getUUID());

        if (state == null || !Objects.equals(state.entity, contraption)) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            state = new RotationState(contraption, true, false);
            states.put(player.getUUID(), state);
            persp.enable(state.entity.pitch, state.entity.yaw);
        } else {
            state.ticksSinceLastUpdate = 0;
        }
    }

    private void tickState(LocalPlayer player) {
        var state = states.get(player.getUUID());
        var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        persp.setLean(state.entity.pitch);
        persp.setYaw(state.entity.yaw);
        player.setYRot(player.getYRot() + state.getYawDelta());
        player.setYBodyRot(player.getYRot());

        if(state.standingState && !state.isMounted) {
            state.ticksSinceLastUpdate += 1;

            if(state.ticksSinceLastUpdate > 5) {
                state.shouldTickState = false;
            }
        }
    }

    public void onTickPlayer(final Player player) {
        if(player instanceof LocalPlayer localPlayer && states.containsKey(player.getUUID())) {
            var state = states.get(player.getUUID());

            if(state.shouldTickState) {
                tickState(localPlayer);
            } else {
                var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
                persp.diminish();

                if(persp.diminished()) {
                    states.remove(player.getUUID());
                    persp.disable();
                }
            }
        }
    }
}
