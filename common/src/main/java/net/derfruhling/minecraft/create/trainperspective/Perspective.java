package net.derfruhling.minecraft.create.trainperspective;

public interface Perspective {
    void enable(float initialLean, float initialYaw);
    void disable();
    boolean isEnabled();
    void setLean(float lean);
    void setYaw(float yaw);
    float getLean();
    float getYaw();

    default void diminish() {
        setLean(getLean() * 0.97f);
        setYaw(getYaw() * 0.97f);
    }
}
