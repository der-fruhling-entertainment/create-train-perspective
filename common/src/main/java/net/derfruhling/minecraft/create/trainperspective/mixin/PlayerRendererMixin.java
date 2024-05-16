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
    @Unique private float ctp$lean = 0.0f, ctp$yaw = 0.0f;

    public void ctp$enable(float initialLean, float initialYaw) {
        ctp$perspectiveActive = true;
        ctp$lean = initialLean;
        ctp$yaw = initialYaw;
    }

    public void ctp$disable() {
        ctp$perspectiveActive = false;
        ctp$lean = 0.0f;
    }

    public void ctp$setLean(float lean) {
        ctp$lean = lean;
    }

    public void ctp$setYaw(float yaw) {
        ctp$yaw = yaw;
    }

    public float ctp$getLean() {
        return ctp$lean;
    }

    public float ctp$getYaw() {
        return ctp$yaw;
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
