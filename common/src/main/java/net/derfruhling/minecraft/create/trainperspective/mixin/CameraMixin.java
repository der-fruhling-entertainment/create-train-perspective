package net.derfruhling.minecraft.create.trainperspective.mixin;

import net.derfruhling.minecraft.create.trainperspective.PlayerPerspectiveBehavior;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow private Entity entity;

    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0), index = 1)
    public float modifyXRotPrimary(float xRot) {
        if(entity instanceof LocalPlayer player) {
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            return xRot - persp.getLean();
        } else return xRot;
    }

    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1), index = 1)
    public float modifyXRotThirdPersonMirrored(float invertedXRot) {
        if(entity instanceof LocalPlayer player) {
            var persp = (PlayerPerspectiveBehavior) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            return invertedXRot + persp.getLean();
        } else return invertedXRot;
    }
}
