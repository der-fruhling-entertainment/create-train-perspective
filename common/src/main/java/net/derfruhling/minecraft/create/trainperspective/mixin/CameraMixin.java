package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.create.trainperspective.Camera3D;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
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

    @Shadow protected abstract void setRotation(float f, float g);

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
    private float modifyRoll(float original) {
        return original + ctp$zRot;
    }

    @Unique
    public float c3d$getZRot() {
        return this.ctp$zRot;
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void modifyRotationsPrimary(Camera instance, float y, float x, @Local(argsOnly = true, ordinal = 1) boolean isThirdPerson) {
        if(entity instanceof LocalPlayer player && !isThirdPerson) {
            var persp = (Perspective) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            ctp$zRot = persp.getLean() * Mth.cos((persp.getYaw() - y) * Mth.DEG_TO_RAD);
            setRotation(
                    MixinUtil.applyDirectionYRotChange(persp, x, y),
                    MixinUtil.applyDirectionXRotChange(persp, x, y)
            );
        } else {
            ctp$zRot = 0;
            setRotation(y, x);
        }
    }
}
