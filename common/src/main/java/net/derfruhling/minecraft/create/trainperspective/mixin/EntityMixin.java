package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.create.trainperspective.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@Implements({@Interface(iface = Perspective.class, prefix = "ctp$")})
@Environment(EnvType.CLIENT)
public abstract class EntityMixin {
    @Shadow @Nullable private Entity vehicle;
    @Shadow private Level level;

    @Unique private boolean ctp$perspectiveActive = false;
    @Unique private float ctp$lean = 0.0f, ctp$yaw = 0.0f, ctp$oldLean = 0.0f, ctp$oldYaw = 0.0f;
    @Unique private @Nullable RotationState ctp$currentState = null;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "RETURN", ordinal = 4))
    public void onMount(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        var self = (Entity)(Object)this;
        if(Conditional.shouldApplyPerspectiveTo(self)) {
            CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(true, self, entity);
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    public void onDismount(CallbackInfo ci) {
        if(vehicle != null) {
            var self = (Entity)(Object)this;
            if(Conditional.shouldApplyPerspectiveTo(self)) {
                CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(false, self, vehicle);
            }
        }
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 1, argsOnly = true)
    public float modifyPitch(float pitch, @Local(argsOnly = true, index = 2) float yaw) {
        if (this.level.isClientSide) {
            if (((Entity)(Object)this) instanceof Perspective persp
                && persp.isEnabled()
                && Conditional.shouldApplyPerspectiveTo((Entity)(Object)this)) {
                return MixinUtil.applyDirectionXRotChange(persp, pitch, yaw, 1.0f);
            } else return pitch;
        } else return pitch;
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 2, argsOnly = true)
    public float modifyYaw(float yaw, @Local(argsOnly = true, index = 1) float pitch) {
        if (this.level.isClientSide) {
            if (((Entity)(Object)this) instanceof Perspective persp
                && persp.isEnabled()
                && Conditional.shouldApplyPerspectiveTo((Entity)(Object)this)) {
                return yaw + MixinUtil.getExtraYRot(persp, pitch, yaw, 1.0f);
            } else return yaw;
        } else return yaw;
    }

    public void ctp$enable(float initialLean, float initialYaw) {
        ctp$perspectiveActive = true;
        ctp$lean = initialLean;
        ctp$yaw = initialYaw;
        ctp$oldLean = initialLean;
        ctp$oldYaw = initialYaw;
    }

    public void ctp$disable() {
        ctp$perspectiveActive = false;
        ctp$lean = 0.0f;
        ctp$yaw = 0.0f;
        ctp$oldLean = 0.0f;
        ctp$oldYaw = 0.0f;
    }

    public boolean ctp$isEnabled() {
        return ctp$perspectiveActive;
    }

    public void ctp$setLean(float lean) {
        ctp$oldLean = ctp$lean;
        ctp$lean = lean;
    }

    public void ctp$setYaw(float yaw) {
        // some configurations flip between 0 and 360 constantly
        // adjust accordingly
        ctp$oldYaw = ctp$yaw;
        ctp$yaw = yaw;

        while (ctp$yaw - ctp$oldYaw < -180.0f) {
            ctp$oldYaw -= 360.0f;
        }

        while (ctp$yaw - ctp$oldYaw >= 180.0f) {
            ctp$oldYaw += 360.0f;
        }
    }

    public float ctp$getLean(float f) {
        if(f == 1.0f) return ctp$lean;
        return Mth.lerp(f, ctp$oldLean, ctp$lean);
    }

    public float ctp$getYaw(float f) {
        if(f == 1.0f) return ctp$yaw;
        return Mth.lerp(f, ctp$oldYaw, ctp$yaw);
    }

    public @Nullable RotationState ctp$getRotationState() {
        return ctp$currentState;
    }

    public void ctp$setRotationState(@Nullable RotationState state) {
        ctp$currentState = state;
    }
}
