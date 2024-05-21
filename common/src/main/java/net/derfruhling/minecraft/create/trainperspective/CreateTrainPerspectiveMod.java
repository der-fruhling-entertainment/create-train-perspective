package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class CreateTrainPerspectiveMod {
    public static final String MODID = "create_train_perspective";
    public static CreateTrainPerspectiveMod INSTANCE;

    public CreateTrainPerspectiveMod() {
        TickEvent.PLAYER_POST.register(this::tickEntity);
        ClientTickEvent.CLIENT_PRE.register(instance -> {
            ModConfig.tick();
        });
        INSTANCE = this;
    }

    public void onEntityMountEvent(boolean isMounting, Entity entityMounting, Entity entityBeingMounted) {
        if(
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityMounting) instanceof Perspective persp &&
                entityBeingMounted instanceof CarriageContraptionEntity contraption
        ) {
            if(isMounting) {
                onEntityMount(persp, contraption);
            } else {
                onEntityDismount(persp);
            }
        }
    }

    public void onEntityMount(Perspective persp, CarriageContraptionEntity contraption) {
        if(persp.getRotationState() == null) {
            var state = new RotationState(contraption, false, true);
            persp.setRotationState(state);
            var carriage = state.getContraption();
            assert carriage != null;
            persp.enable(carriage.pitch, carriage.yaw);
        } else {
            var state = persp.getRotationState();
            state.onMounted();
        }
    }

    private void onEntityDismount(Perspective persp) {
        if(persp.getRotationState() != null) {
            persp.setRotationState(null);
            persp.disable();
        }
    }

    public void tickStandingEntity(final CarriageContraptionEntity contraption, final Entity entity) {
        if(entity.getVehicle() != null) return;

        var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        var state = persp.getRotationState();

        if (state == null || !Objects.equals(state.getContraption(), contraption)) {
            state = new RotationState(contraption, true, false);
            persp.setRotationState(state);
            var carriage = state.getContraption();
            assert carriage != null;
            persp.enable(carriage.pitch, carriage.yaw);
        } else {
            state.update();
        }
    }

    private void tickPerspectiveState(Entity player, Perspective persp, RotationState state) {
        var carriage = state.getContraption();
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

    public void tickEntity(final Entity entity) {
        if(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof Perspective persp
           && persp.getRotationState() != null
           && Conditional.shouldApplyPerspectiveTo(entity)) {
            var state = persp.getRotationState();
            assert state != null;

            if(state.shouldTickState()) {
                tickPerspectiveState(entity, persp, state);
            } else {
                persp.diminish();

                if(persp.isDiminished()) {
                    persp.setRotationState(null);
                    persp.disable();
                }
            }
        }
    }
}
