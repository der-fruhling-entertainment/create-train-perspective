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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        public boolean standingState, isMounted, shouldTickState = true;

        public RotationState(CarriageContraptionEntity entity, boolean standingState, boolean isMounted) {
            this.entity = entity;
            lastYaw = entity.yaw;
            this.standingState = standingState;
            this.isMounted = isMounted;
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
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            if(isMounting) {
                if(!states.containsKey(entityMounting.getUUID())) {
                    var state = new RotationState(contraption, false, true);
                    states.put(entityMounting.getUUID(), state);
                    persp.enable(state.entity.pitch, state.entity.yaw);
                } else {
                    states.get(entityMounting.getUUID()).isMounted = true;
                }
            } else {
                if(states.containsKey(entityMounting.getUUID())) {
                    if(states.get(entityMounting.getUUID()).standingState) {
                        states.get(entityMounting.getUUID()).isMounted = false;
                    } else {
                        states.remove(entityMounting.getUUID());
                        persp.disable();
                    }
                }
            }
        }
    }

    public void tickStandingPlayers(final CarriageContraptionEntity contraption) {
        for(Map.Entry<Entity, MutableInt> entry : contraption.collidingEntities.entrySet()) {
            var entity = entry.getKey();
            var ticks = entry.getValue();
            if(entity instanceof LocalPlayer player) {
                if(player.getVehicle() != null) continue;

                var state = states.get(player.getUUID());
                if (state == null) {
                    var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
                    state = new RotationState(contraption, true, false);
                    states.put(player.getUUID(), state);
                    persp.enable(state.entity.pitch, state.entity.yaw);
                } else if(ticks.getValue() >= 2) {
                    state.shouldTickState = false;
                } else if(!state.shouldTickState) {
                    state.shouldTickState = true;
                }
            }
        }
    }

    private void tickState(LocalPlayer player) {
        var state = states.get(player.getUUID());
        var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        persp.setLean(state.entity.pitch);
        persp.setYaw(state.entity.yaw);
        player.setYRot(player.getYRot() + state.getYawDelta());
        player.setYBodyRot(player.getYRot());
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
