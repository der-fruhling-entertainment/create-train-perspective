package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 2, shift = At.Shift.BEFORE))
    public void applyLevelRotations(float f, long l, PoseStack poseStack, CallbackInfo ci) {
        if (Conditional.shouldApplyPerspectiveTo(mainCamera.getEntity())) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(MixinUtil.asCamera3D(mainCamera).getZRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(MixinUtil.asCamera3D(mainCamera).getExtraYRot()));
        }
    }
}
