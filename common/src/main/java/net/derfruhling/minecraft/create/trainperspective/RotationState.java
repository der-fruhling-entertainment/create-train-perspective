package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.jetbrains.annotations.Nullable;

public class RotationState implements RotationStateKeeper {
    private CarriageContraptionEntity entity;
    private float lastYaw;
    private final boolean standingState;
    private boolean isMounted;
    private boolean shouldTickState = true;
    private int ticksSinceLastUpdate = 0;

    public RotationState(CarriageContraptionEntity entity, boolean standingState, boolean isMounted) {
        this.entity = entity;
        lastYaw = entity.yaw;
        this.standingState = standingState;
        this.isMounted = isMounted;
    }

    @Override
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

    @Override
    public @Nullable CarriageContraptionEntity getCarriageEntity() {
        return entity;
    }

    @Override
    public void setCarriageEntity(@Nullable CarriageContraptionEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isStanding() {
        return standingState;
    }

    @Override
    public boolean isMounted() {
        return isMounted;
    }

    @Override
    public boolean shouldTickState() {
        return shouldTickState;
    }

    @Override
    public void onMounted() {
        this.isMounted = true;
        this.shouldTickState = true;
    }

    @Override
    public void onDismount() {
        this.isMounted = false;
    }

    @Override
    public void setShouldTickState(boolean shouldTickState) {
        this.shouldTickState = shouldTickState;
    }

    @Override
    public int getTicksSinceLastUpdate() {
        return ticksSinceLastUpdate;
    }

    @Override
    public void update() {
        ticksSinceLastUpdate = 0;
    }

    @Override
    public void tick() {
        ticksSinceLastUpdate += 1;
    }
}
