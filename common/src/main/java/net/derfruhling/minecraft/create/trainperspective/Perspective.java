package net.derfruhling.minecraft.create.trainperspective;

import net.minecraft.util.Mth;

public interface Perspective {
    void enable(float initialLean, float initialYaw);
    void disable();
    boolean isEnabled();
    void setLean(float lean);
    void setYaw(float yaw);
    float getLean(float f);
    float getYaw(float f);

    default void diminish() {
        setLean(getLean(1.0f) * 0.9f);
    }

    default boolean diminished() {
        return Mth.abs(getLean(1.0f)) < 0.02f;
    }
}
