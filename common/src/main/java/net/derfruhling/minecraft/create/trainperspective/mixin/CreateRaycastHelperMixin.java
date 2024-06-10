package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RaycastHelper.class)
public class CreateRaycastHelperMixin {
    @ModifyReturnValue(method = "getTraceOrigin", at = @At("RETURN"))
    private static Vec3 applyLeaning(Vec3 original, Player player) {
        if(Conditional.shouldApplyPerspectiveTo(player) && player instanceof Perspective persp) {
            return MixinUtil.applyStandingCameraRotation(player, original, persp, 1.0f);
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 4)
    private static float modifyPitch(float pitch, Player player) {
        if(player instanceof Perspective persp
           && Conditional.shouldApplyPerspectiveTo(player)) {
            return MixinUtil.applyDirectionXRotChange(persp, pitch, player.getYRot(), 1.0f);
        } else return pitch;
    }

    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 5)
    private static float modifyYaw(float yaw, Player player) {
        if(player instanceof Perspective persp
           && Conditional.shouldApplyPerspectiveTo(player)) {
            return yaw + MixinUtil.getExtraYRot(persp, player.getXRot(), yaw, 1.0f);
        } else return yaw;
    }
}
