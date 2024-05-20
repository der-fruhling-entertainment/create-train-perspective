package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.create.trainperspective.Camera3D;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
@Implements({@Interface(iface = Camera3D.class, prefix = "c3d$")})
@Environment(EnvType.CLIENT)
public abstract class CameraMixin {
    @Shadow private Entity entity;
    @Unique private float ctp$zRot;
    @Unique private float ctp$extraYRot;

    @Shadow protected abstract void setRotation(float f, float g);

    @Shadow @Final private Quaternionf rotation;

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
    private float modifyRoll(float original) {
        return original + (ctp$zRot * Mth.DEG_TO_RAD);
    }

    @Inject(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", shift = At.Shift.AFTER))
    private void modifyQuaternion(float f, float g, CallbackInfo ci) {
        //this.rotation.rotateY(ctp$extraYRot * Mth.DEG_TO_RAD);
    }

    @Unique
    public float c3d$getZRot() {
        return this.ctp$zRot;
    }

    @Unique
    public float c3d$getExtraYRot() {
        return this.ctp$extraYRot;
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void modifyRotationsPrimary(Camera instance,
                                       float y,
                                       float x,
                                       @Local(argsOnly = true, ordinal = 0) boolean isThirdPerson,
                                       @Local(argsOnly = true) float f) {
        if(entity instanceof AbstractClientPlayer player && !isThirdPerson) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            ctp$zRot = persp.getLean(f)
                       * Mth.cos((persp.getYaw(f) - y) * Mth.DEG_TO_RAD)
                       * Mth.sin((x * Mth.DEG_TO_RAD + Mth.PI) / 2.0f);
            ctp$extraYRot = MixinUtil.getExtraYRot(persp, x, y, f);
            setRotation(
                    y,
                    MixinUtil.applyDirectionXRotChange(persp, x, y, f)
            );
        } else {
            ctp$zRot = 0;
            ctp$extraYRot = 0;
            setRotation(y, x);
        }
    }
}
