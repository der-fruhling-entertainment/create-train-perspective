package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.util.Mth;
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
        if (f == 1.0f) return ref.pitch * getValueScale();
        return Mth.lerp(f, ref.prevPitch * getPrevValueScale(), ref.pitch * getValueScale());
    }

    default float getYaw(float f) {
        var ref = getReference();
        if (ref == null) return 0.0f;
        if (f == 1.0f) return ref.yaw * getValueScale();
        return Mth.lerp(f, ref.prevYaw * getPrevValueScale(), ref.yaw * getValueScale());
    }

    @Nullable
    RotationState getRotationState();

    void setRotationState(@Nullable RotationState state);

    void diminish();

    float getPrevValueScale();

    // this must not be named getScale()!!!!
    // Entity (or something) has a method also called getScale() that conflicts
    // with this method in a fuckey way.
    float getValueScale();

    default boolean isDiminished() {
        return getValueScale() < 0.00025f;
    }
}
