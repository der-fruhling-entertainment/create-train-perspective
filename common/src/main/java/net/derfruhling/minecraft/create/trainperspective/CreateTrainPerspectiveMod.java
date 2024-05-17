package net.derfruhling.minecraft.create.trainperspective;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    private final HashMap<UUID, RotationStateKeeper> states = new HashMap<>();

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
                    var carriage = state.getCarriageEntity();
                    assert carriage != null;
                    persp.enable(carriage.pitch, carriage.yaw);
                } else {
                    var state = states.get(entityMounting.getUUID());
                    state.onMounted();
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

        if (state == null || !Objects.equals(state.getCarriageEntity(), contraption)) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            state = new RotationState(contraption, true, false);
            states.put(player.getUUID(), state);
            var carriage = state.getCarriageEntity();
            assert carriage != null;
            persp.enable(carriage.pitch, carriage.yaw);
        } else {
            state.update();
        }
    }

    private void tickState(LocalPlayer player) {
        var state = states.get(player.getUUID());
        var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        var carriage = state.getCarriageEntity();
        if(carriage == null) return;
        persp.setLean(carriage.pitch);
        persp.setYaw(carriage.yaw);
        player.setYRot(player.getYRot() + state.getYawDelta());
        player.setYBodyRot(player.getYRot());

        if(state.isStanding() && !state.isMounted()) {
            state.tick();

            if(state.getTicksSinceLastUpdate() > 5) {
                state.setShouldTickState(false);
            }
        }
    }

    public void onTickPlayer(final Player player) {
        if(player instanceof LocalPlayer localPlayer && states.containsKey(player.getUUID())) {
            var state = states.get(player.getUUID());

            if(state.shouldTickState()) {
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
