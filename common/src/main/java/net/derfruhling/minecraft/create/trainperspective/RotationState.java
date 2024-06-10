package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.jetbrains.annotations.Nullable;

public class RotationState {
    private boolean isStandingState;
    private CarriageContraptionEntity contraption;
    private float lastRecordedYaw;
    private boolean shouldTickState = true;
    private int ticksSinceLastUpdate = 0;

    public RotationState(CarriageContraptionEntity contraption, boolean isStandingState) {
        this.contraption = contraption;
        lastRecordedYaw = contraption.yaw;
        this.isStandingState = isStandingState;
    }

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

    public @Nullable CarriageContraptionEntity getContraption() {
        return contraption;
    }

    public void setCarriageEntity(@Nullable CarriageContraptionEntity contraption) {
        this.contraption = contraption;
    }

    public boolean isStanding() {
        return isStandingState;
    }

    public boolean isSeated() {
        return !isStandingState;
    }

    public boolean shouldTickState() {
        return shouldTickState;
    }

    public void onMounted() {
        this.isStandingState = false;
        this.shouldTickState = true;
    }

    public void onDismount() {
        this.isStandingState = true;
    }

    public void setShouldTickState(boolean shouldTickState) {
        this.shouldTickState = shouldTickState;
    }

    public int getTicksSinceLastUpdate() {
        return ticksSinceLastUpdate;
    }

    public void update() {
        ticksSinceLastUpdate = 0;
    }

    public void tick() {
        ticksSinceLastUpdate += 1;
    }
}
