package net.derfruhling.minecraft.create.trainperspective;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;

public class MixinUtil {
    private MixinUtil() {}

    public static Camera3D asCamera3D(Camera camera) {
        return (Camera3D) camera;
    }

    private static float invCos(float x) {
        return Mth.cos(x + Mth.PI);
    }

    public static float applyDirectionXRotChange(Perspective persp, float xRot, float yRot, float f) {
        return xRot - persp.getLean(f) * Mth.sin((persp.getYaw(f) - yRot) * Mth.DEG_TO_RAD);
    }

    public static float getExtraYRot(Perspective persp, float xRot, float yRot, float f) {
        return persp.getLean(f) * (xRot / 90.0f) * invCos((persp.getYaw(f) - yRot) * Mth.DEG_TO_RAD);
    }
}
