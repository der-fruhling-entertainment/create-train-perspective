package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class CreateTrainPerspectiveMod {
    public static final String MODID = "create_train_perspective";
    public static CreateTrainPerspectiveMod INSTANCE;

    public CreateTrainPerspectiveMod() {
        ClientTickEvent.CLIENT_PRE.register(instance -> {
            ModConfig.tick();
        });
        INSTANCE = this;
    }

    public void onEntityMountEvent(boolean isMounting, Entity entityMounting, Entity entityBeingMounted) {
        if (
                entityMounting instanceof Perspective persp &&
                        entityBeingMounted instanceof CarriageContraptionEntity contraption
        ) {
            if (isMounting) {
                onEntityMount(persp, contraption);
            } else {
                onEntityDismount(persp);
            }
        }
    }

    public void onEntityMount(Perspective persp, CarriageContraptionEntity contraption) {
        if (persp.getRotationState() == null) {
            var state = new RotationState(contraption, false);
            persp.setRotationState(state);
            var carriage = state.getContraption();
            assert carriage != null;
            persp.enable(carriage);
        } else {
            var state = persp.getRotationState();
            state.onMounted();
        }
    }

    private void onEntityDismount(Perspective persp) {
        if (persp.getRotationState() != null) {
            persp.getRotationState().onDismount();
        }
    }

    public void tickStandingEntity(final CarriageContraptionEntity contraption, final Entity entity) {
        if (entity.getVehicle() != null) return;
        if (!(entity instanceof Perspective persp)) return;

        var state = persp.getRotationState();

        if (state == null || !Objects.equals(state.getContraption(), contraption)) {
            state = new RotationState(contraption, true);
            persp.setRotationState(state);
            var carriage = state.getContraption();
            assert carriage != null;
            persp.enable(carriage);
        } else {
            state.update();
        }
    }

    private void tickPerspectiveState(Entity player, Perspective persp, RotationState state) {
        var carriage = state.getContraption();
        if (carriage == null) return;
        persp.setReference(carriage);
        player.setYRot(player.getYRot() + state.getYawDelta());
        player.setYBodyRot(player.getYRot());

        if (state.isStanding() && !state.isSeated()) {
            state.tick();

            if (state.getTicksSinceLastUpdate() > 5) {
                state.setShouldTickState(false);
            }
        }
    }

    public void tickEntity(Entity entity, Perspective persp) {
        if (persp.getRotationState() != null) {
            var state = persp.getRotationState();
            assert state != null;

            if (state.shouldTickState()) {
                tickPerspectiveState(entity, persp, state);
            } else {
                persp.diminish();

                if (persp.isDiminished()) {
                    persp.setRotationState(null);
                    persp.disable();
                }
            }
        }
    }
}
