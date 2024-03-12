package net.derfruhling.minecraft.create.trainperspective;

public interface PlayerPerspectiveBehavior {
    void enable(float initialLean);
    void disable();
    void setLean(float lean);
}
