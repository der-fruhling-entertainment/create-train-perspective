package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class LivingEntityRendererMixin {
    @Inject(method = "setupRotations", at = @At("HEAD"))
    protected void setupRotations(LivingEntity livingEntity, PoseStack poseStack, float f, float g, float h, CallbackInfo ci) {
        if (Conditional.shouldApplyPerspectiveTo(livingEntity)) {
            Perspective persp = (Perspective) livingEntity;
            float height = 0;

            if (livingEntity.getVehicle() != null) {
                height = livingEntity.getEyeHeight();
            }

            var lean = persp.getLean(h);
            var yaw = persp.getYaw(h);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.cos(Mth.DEG_TO_RAD * yaw) * lean), 0, height, 0);
            poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.sin(Mth.DEG_TO_RAD * yaw) * -lean), 0, height, 0);
        }
    }
}
