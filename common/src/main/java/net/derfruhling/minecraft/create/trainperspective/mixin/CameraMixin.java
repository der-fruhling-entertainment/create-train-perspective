package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Shadow @Final private Quaternion rotation;

    @Inject(method = "setRotation", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Quaternion;mul(Lcom/mojang/math/Quaternion;)V", shift = At.Shift.AFTER, ordinal = 1))
    private void applyRoll(float y, float x, CallbackInfo ci) {
        rotation.mul(Vector3f.ZP.rotationDegrees(ctp$zRot));
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
