package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Perspective {
    void enable(CarriageContraptionEntity entity);

    void disable();

    boolean isEnabled();

    void setReference(CarriageContraptionEntity entity);

    @Nullable
    CarriageContraptionEntity getReference();

    default float getLean(float f) {
        var ref = getReference();
        if (ref == null) return 0.0f;
        if (f == 1.0f) return ref.pitch * getScale();
        return Mth.lerp(f, ref.prevPitch * getPrevScale(), ref.pitch * getScale());
    }

    default float getYaw(float f) {
        var ref = getReference();
        if (ref == null) return 0.0f;
        if (f == 1.0f) return ref.yaw * getScale();
        return Mth.lerp(f, ref.prevYaw * getPrevScale(), ref.yaw * getScale());
    }

    @Nullable
    RotationState getRotationState();

    void setRotationState(@Nullable RotationState state);

    void diminish();

    float getPrevScale();

    float getScale();

    default boolean isDiminished() {
        return Mth.abs(getLean(1.0f)) < 0.01f;
    }
}
