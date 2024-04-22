package net.derfruhling.minecraft.create.trainperspective;

import net.derfruhling.minecraft.create.trainperspective.mixin.CameraMixin;
import net.minecraft.client.Camera;

public class MixUtil {
    private MixUtil() {}

    public static Camera3D asCamera3D(Camera camera) {
        return (Camera3D) camera;
    }
}
