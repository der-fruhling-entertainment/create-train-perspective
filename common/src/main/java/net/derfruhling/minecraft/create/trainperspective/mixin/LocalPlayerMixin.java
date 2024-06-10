package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// If anything else tries to @Overwrite getViewYRot, use their changes.
@Mixin(value = LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow @Final protected Minecraft minecraft;

    @WrapOperation(method = "getViewYRot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
    public boolean isPassenger(LocalPlayer localPlayer, Operation<Boolean> original) {
        var perspective = (Perspective) localPlayer;
        return original.call(localPlayer) || perspective.isEnabled();
    }
}
