package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
@Implements({@Interface(iface = Perspective.class, prefix = "ctp$")})
@Environment(EnvType.CLIENT)
public class PlayerRendererMixin {
    @Unique private boolean ctp$perspectiveActive = false;
    @Unique private float ctp$lean = 0.0f, ctp$yaw = 0.0f, ctp$oldLean = 0.0f, ctp$oldYaw = 0.0f;

    public void ctp$enable(float initialLean, float initialYaw) {
        ctp$perspectiveActive = true;
        ctp$lean = initialLean;
        ctp$yaw = initialYaw;
        ctp$oldLean = initialLean;
        ctp$oldYaw = initialYaw;
    }

    public void ctp$disable() {
        ctp$perspectiveActive = false;
        ctp$lean = 0.0f;
        ctp$yaw = 0.0f;
        ctp$oldLean = 0.0f;
        ctp$oldYaw = 0.0f;
    }

    public boolean ctp$isEnabled() {
        return ctp$perspectiveActive;
    }

    public void ctp$setLean(float lean) {
        ctp$oldLean = ctp$lean;
        ctp$lean = lean;
    }

    public void ctp$setYaw(float yaw) {
        // some configurations flip between 0 and 360 constantly
        // adjust accordingly
        ctp$oldYaw = ctp$yaw;
        ctp$yaw = yaw;

        while (ctp$yaw - ctp$oldYaw < -180.0f) {
            ctp$oldYaw -= 360.0f;
        }

        while (ctp$yaw - ctp$oldYaw >= 180.0f) {
            ctp$oldYaw += 360.0f;
        }
    }

    public float ctp$getLean(float f) {
        if(f == 1.0f) return ctp$lean;
        return Mth.lerp(f, ctp$oldLean, ctp$lean);
    }

    public float ctp$getYaw(float f) {
        if(f == 1.0f) return ctp$yaw;
        return Mth.lerp(f, ctp$oldYaw, ctp$yaw);
    }

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At(
                    value = "HEAD"
            )
    )
    protected void setupRotations(AbstractClientPlayer p_117802_, PoseStack p_117803_, float p_117804_, float p_117805_, float p_117806_, CallbackInfo ci) {
        if(ctp$perspectiveActive) {
            float height = 0;

            if(p_117802_.getVehicle() != null) {
                height = 1.4f;
            }

            p_117803_.rotateAround(Axis.ZP.rotationDegrees(Mth.cos(Mth.DEG_TO_RAD * ctp$yaw) * ctp$lean), 0, height, 0);
            p_117803_.rotateAround(Axis.XP.rotationDegrees(Mth.sin(Mth.DEG_TO_RAD * ctp$yaw) * -ctp$lean), 0, height, 0);
        }
    }
}
