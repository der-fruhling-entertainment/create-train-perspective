package net.derfruhling.minecraft.create.trainperspective;

public interface Perspective {
    void enable(float initialLean, float initialYaw);
    void disable();
    boolean isEnabled();
    void setLean(float lean);
    void setYaw(float yaw);
    float getLean(float f);
    float getYaw(float f);

    default void diminish() {
        setLean(getLean(1.0f) * 0.97f);
        setYaw(getYaw(1.0f) * 0.97f);
    }
}
