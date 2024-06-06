package net.derfruhling.minecraft.create.trainperspective;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class MixinUtil {
    private MixinUtil() {}

    public static Camera3D asCamera3D(Camera camera) {
        return (Camera3D) camera;
    }

    private static float invCos(float x) {
        return Mth.cos(x + Mth.PI);
    }

    public static float applyDirectionXRotChange(Perspective persp, float xRot, float yRot, float f) {
        return xRot - persp.getLean(f)
                      * ModConfig.INSTANCE.leanMagnitude
                      * Mth.sin((persp.getYaw(f) - yRot) * Mth.DEG_TO_RAD);
    }

    public static float getExtraYRot(Perspective persp, float xRot, float yRot, float f) {
        return persp.getLean(f) * (xRot / 90.0f) * invCos((persp.getYaw(f) - yRot) * Mth.DEG_TO_RAD);
    }

    public static Vector3d applyStandingCameraRotation(Player player, double x, double y, double z, Perspective persp, float f) {
        var lean = persp.getLean(f) * Mth.DEG_TO_RAD;
        var yaw = persp.getYaw(f) * Mth.DEG_TO_RAD;
        var height = player.getEyeHeight();
        var newY = y + ((height * Mth.cos(lean)) - height);
        var leanSin = Mth.sin(lean);
        var newZ = z - (height * Mth.sin(yaw) * leanSin);
        var newX = x - (height * Mth.cos(yaw) * leanSin);

        return new Vector3d(newX, newY, newZ);
    }

    public static Vec3 applyStandingCameraRotation(Player player, Vec3 v, Perspective persp, float f) {
        var vec = applyStandingCameraRotation(player, v.x, v.y, v.z, persp, f);
        return new Vec3(vec.x, vec.y, vec.z);
    }
}
