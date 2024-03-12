package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.derfruhling.minecraft.create.trainperspective.PlayerPerspectiveBehavior;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
@Implements({@Interface(iface = PlayerPerspectiveBehavior.class, prefix = "ctp$")})
public class PlayerRendererMixin {
    @Unique private boolean ctp$perspectiveActive = false;
    @Unique private float ctp$lean = 0.0f;

    public void ctp$enable(float initialLean) {
        ctp$perspectiveActive = true;
        ctp$lean = initialLean;
    }

    public void ctp$disable() {
        ctp$perspectiveActive = false;
        ctp$lean = 0.0f;
    }

    public void ctp$setLean(float lean) {
        ctp$lean = lean;
    }

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"
            )
    )
    protected void setupRotations(AbstractClientPlayer p_117802_, PoseStack p_117803_, float p_117804_, float p_117805_, float p_117806_, CallbackInfo ci) {
        if(ctp$perspectiveActive) {
            p_117803_.rotateAround(Axis.ZP.rotationDegrees(ctp$lean), 0, 1.3f, 0);
        }
    }
}
