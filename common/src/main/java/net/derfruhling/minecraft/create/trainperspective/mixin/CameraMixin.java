package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.create.trainperspective.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
@Implements({@Interface(iface = Camera3D.class, prefix = "c3d$")})
@Environment(EnvType.CLIENT)
public abstract class CameraMixin {
    @Shadow private Entity entity;
    @Unique private float ctp$zRot;
    @Unique private float ctp$extraYRot;

    @Shadow protected abstract void setRotation(float f, float g);

    @Shadow @Final private Quaternionf rotation;

    @Shadow protected abstract void setPosition(double d, double e, double f);

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
    private float modifyRoll(float original) {
        return original + (ctp$zRot * Mth.DEG_TO_RAD);
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
    public void modifyRotations(Camera instance,
                                float y,
                                float x,
                                @Local(argsOnly = true, ordinal = 0) boolean isThirdPerson,
                                @Local(argsOnly = true) float f) {
        if(entity instanceof AbstractClientPlayer player
           && Conditional.shouldApplyPerspectiveTo(entity)
           && Conditional.shouldApplyLeaning()
           && !isThirdPerson) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

            if(Conditional.shouldApplyRolling()) {
                ctp$zRot = persp.getLean(f)
                           * ModConfig.INSTANCE.rollMagnitude
                           * Mth.cos((persp.getYaw(f) - y) * Mth.DEG_TO_RAD)
                           * Mth.sin((x * Mth.DEG_TO_RAD + Mth.PI) / 2.0f);
            }

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

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    public void modifyPosition(Camera instance,
                               double x,
                               double y,
                               double z,
                               @Local(argsOnly = true, ordinal = 0) boolean isThirdPerson,
                               @Local(argsOnly = true) float f) {
        if(entity instanceof AbstractClientPlayer player
                && Conditional.shouldApplyPerspectiveTo(entity)
                && Conditional.shouldApplyLeaning()
                && player.getVehicle() == null
                && !isThirdPerson) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            var newV = MixinUtil.applyStandingCameraRotation(player, x, y, z, persp, f);

            if (ModConfig.INSTANCE.dbgShowStandingTransforms) {
                player.displayClientMessage(Component.literal("%f, %f, %f".formatted(x - newV.x, y - newV.y, z - newV.z)), true);
            }

            setPosition(newV.x, newV.y, newV.z);
        } else {
            setPosition(x, y, z);
        }
    }
}
