package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.derfruhling.minecraft.create.trainperspective.RotationState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
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
    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At(
                    value = "HEAD"
            )
    )
    protected void setupRotations(AbstractClientPlayer clientPlayer, PoseStack p_117803_, float p_117804_, float p_117805_, float f, CallbackInfo ci) {
        if(Conditional.shouldApplyPerspectiveTo(clientPlayer)) {
            Perspective persp = (Perspective) clientPlayer;
            float height = 0;

            if(clientPlayer.getVehicle() != null) {
                height = clientPlayer.getEyeHeight();
            }

            var lean = persp.getLean(f);
            var yaw = persp.getYaw(f);
            p_117803_.rotateAround(Axis.ZP.rotationDegrees(Mth.cos(Mth.DEG_TO_RAD * yaw) * lean), 0, height, 0);
            p_117803_.rotateAround(Axis.XP.rotationDegrees(Mth.sin(Mth.DEG_TO_RAD * yaw) * -lean), 0, height, 0);
        }
    }
}
