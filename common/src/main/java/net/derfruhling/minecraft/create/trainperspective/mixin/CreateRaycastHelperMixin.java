package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.simibubi.create.foundation.utility.RaycastHelper;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RaycastHelper.class)
public class CreateRaycastHelperMixin {
    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 4)
    private static float modifyXRot(float value, Player player) {
        if(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof Perspective persp) {
            return MixinUtil.applyDirectionXRotChange(persp, value, player.getYRot(), 1.0f);
        } else return value;
    }

    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 5)
    private static float modifyYRot(float value, Player player) {
        if(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof Perspective persp) {
            return value + MixinUtil.getExtraYRot(persp, player.getXRot(), value, 1.0f);
        } else return value;
    }
}
