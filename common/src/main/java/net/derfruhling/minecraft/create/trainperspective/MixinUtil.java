package net.derfruhling.minecraft.create.trainperspective;

import net.minecraft.client.Camera;

public class MixinUtil {
    private MixinUtil() {}

    public static Camera3D asCamera3D(Camera camera) {
        return (Camera3D) camera;
    }
}
