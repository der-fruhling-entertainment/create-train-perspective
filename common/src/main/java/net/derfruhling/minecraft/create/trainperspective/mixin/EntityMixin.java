package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public abstract class EntityMixin {
    @Shadow @Nullable private Entity vehicle;
    @Shadow private Level level;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "RETURN", ordinal = 4))
    public void onMount(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(true, (Entity)(Object)this, entity);
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    public void onDismount(CallbackInfo ci) {
        if(vehicle != null) {
            CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(false, (Entity)(Object)this, vehicle);
        }
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 1, argsOnly = true)
    public float modifyPitch(float pitch, @Local(argsOnly = true, index = 2) float yaw) {
        if (this.level.isClientSide) {
            if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)(Object)this) instanceof Perspective persp && persp.isEnabled()) {
                return MixinUtil.applyDirectionXRotChange(persp, pitch, yaw, 1.0f);
            } else return pitch;
        } else return pitch;
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 2, argsOnly = true)
    public float modifyYaw(float yaw, @Local(argsOnly = true, index = 1) float pitch) {
        if (this.level.isClientSide) {
            if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)(Object)this) instanceof Perspective persp && persp.isEnabled()) {
                return yaw + MixinUtil.getExtraYRot(persp, pitch, yaw, 1.0f);
            } else return yaw;
        } else return yaw;
    }
}
