package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.jetbrains.annotations.Nullable;

public class RotationState implements RotationStateKeeper {
    private CarriageContraptionEntity contraption;
    private float lastRecordedYaw;
    private final boolean isStandingState;
    private boolean isMounted;
    private boolean shouldTickState = true;
    private int ticksSinceLastUpdate = 0;

    public RotationState(CarriageContraptionEntity contraption, boolean isStandingState, boolean isMounted) {
        this.contraption = contraption;
        lastRecordedYaw = contraption.yaw;
        this.isStandingState = isStandingState;
        this.isMounted = isMounted;
    }

    @Override
    public float getYawDelta() {
        while (contraption.yaw - lastRecordedYaw < -180.0f) {
            lastRecordedYaw -= 360.0f;
        }

        while (contraption.yaw - lastRecordedYaw >= 180.0f) {
            lastRecordedYaw += 360.0f;
        }

        var rotation = contraption.yaw - lastRecordedYaw;
        lastRecordedYaw = contraption.yaw;
        return rotation;
    }

    @Override
    public @Nullable CarriageContraptionEntity getContraption() {
        return contraption;
    }

    @Override
    public void setCarriageEntity(@Nullable CarriageContraptionEntity contraption) {
        this.contraption = contraption;
    }

    @Override
    public boolean isStanding() {
        return isStandingState;
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
