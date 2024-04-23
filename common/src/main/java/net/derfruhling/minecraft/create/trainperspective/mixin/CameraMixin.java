package net.derfruhling.minecraft.create.trainperspective.mixin;

import net.derfruhling.minecraft.create.trainperspective.Camera3D;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
@Implements({@Interface(iface = Camera3D.class, prefix = "ctp$")})
public abstract class CameraMixin {
    @Shadow private Entity entity;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Unique private float ctp$zRot;

    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;

    @Shadow protected abstract void setRotation(float f, float g);

    @Unique
    public void ctp$setRotation3D(float y, float x, float z) {
        this.xRot = x;
        this.yRot = y;
        this.ctp$zRot = z;
        this.rotation.rotationYXZ(-y * 0.017453292F, x * 0.017453292F, z * 0.017453292F);
        this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
        this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
        this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
    }

    @Unique
    public float ctp$getZRot() {
        return this.ctp$zRot;
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void modifyRotationsPrimary(Camera instance, float y, float x) {
        if(entity instanceof LocalPlayer player) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            ctp$setRotation3D(y,
                    x - persp.getLean() * Mth.sin((persp.getYaw() - y) * Mth.DEG_TO_RAD),
                    persp.getLean() * Mth.cos((persp.getYaw() - y) * Mth.DEG_TO_RAD));
        } else setRotation(y, x);
    }
}
