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
import net.minecraft.world.level.BlockGetter;
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
                                BlockGetter blockGetter,
                                Entity entity,
                                boolean isThirdPerson,
                                boolean bl2,
                                float f) {
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
                               BlockGetter blockGetter,
                               Entity entity,
                               boolean isThirdPerson,
                               boolean bl2,
                               float f) {
        if(entity instanceof AbstractClientPlayer player
                && Conditional.shouldApplyPerspectiveTo(entity)
                && Conditional.shouldApplyLeaning()
                && player.getVehicle() == null
                && !isThirdPerson) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            var lean = persp.getLean(f);
            var yaw = persp.getYaw(f);

            var height = player.getEyeHeight();
            // var leanFactor = (1.0f - Mth.cos(2.0f * lean * Mth.DEG_TO_RAD)) / 2.0f;
            var leanFactor = (lean / 90.0f);
            var newY = y - (height * leanFactor);
            var newX = x - Mth.abs(height * height * (Mth.cos(yaw * Mth.DEG_TO_RAD)) * leanFactor);
            var newZ = z - Mth.abs(height * height * (Mth.sin(yaw * Mth.DEG_TO_RAD)) * leanFactor);

            if(ModConfig.INSTANCE.dbgShowStandingTransforms) {
                player.displayClientMessage(Component.literal(String.format("%f, %f, %f", newX - x, newY - y, newZ - z)), true);
            }

            setPosition(newX, newY, newZ);
        } else {
            setPosition(x, y, z);
        }
    }
}
