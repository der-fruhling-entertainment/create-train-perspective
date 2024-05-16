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

    public static float applyDirectionXRotChange(Perspective persp, float xRot, float yRot) {
        return xRot - persp.getLean() * Mth.sin((persp.getYaw() - yRot) * Mth.DEG_TO_RAD);
    }

    public static float getExtraYRot(Perspective persp, float xRot, float yRot) {
        return persp.getLean() * (xRot / 90.0f) * invCos((persp.getYaw() - yRot) * Mth.DEG_TO_RAD);
    }
}
