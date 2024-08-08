package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.derfruhling.minecraft.create.trainperspective.ModConfig;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow public abstract void displayClientMessage(Component arg, boolean bl);

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @WrapOperation(method = "getViewYRot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
    public boolean isPassenger(LocalPlayer localPlayer, Operation<Boolean> original) {
        var perspective = (Perspective) localPlayer;
        return original.call(localPlayer) || perspective.isEnabled();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tickDebugFeatures(CallbackInfo ci) {
        var persp = (Perspective) this;

        if(ModConfig.INSTANCE.dbgShowValueScales) {
            this.displayClientMessage(Component.literal(persp.getValueScale() + ", " + persp.getPrevValueScale()), true);
        }
    }
}
