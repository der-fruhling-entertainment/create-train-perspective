package net.derfruhling.minecraft.create.trainperspective;

public interface PlayerPerspectiveBehavior {
    void enable(float initialLean, float initialYaw);
    void disable();
    void setLean(float lean);
    void setYaw(float yaw);
    float getLean();
}
