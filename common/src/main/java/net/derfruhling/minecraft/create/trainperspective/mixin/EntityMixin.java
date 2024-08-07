package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.derfruhling.minecraft.create.trainperspective.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@Implements({@Interface(iface = Perspective.class, prefix = "ctp$")})
@Environment(EnvType.CLIENT)
public abstract class EntityMixin {
    @Shadow
    @Nullable
    private Entity vehicle;
    @Shadow
    private Level level;

    @Unique
    private boolean ctp$perspectiveActive = false;
    @Unique
    private @Nullable CarriageContraptionEntity ctp$reference = null;
    @Unique
    private float ctp$scale = 1.0f, ctp$prevScale = 1.0f;
    @Unique
    private @Nullable RotationState ctp$currentState = null;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "RETURN", ordinal = 4))
    public void onMount(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        var self = (Entity) (Object) this;
        if (Conditional.shouldApplyPerspectiveTo(self)) {
            CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(true, self, entity);
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    public void onDismount(CallbackInfo ci) {
        if (vehicle != null) {
            var self = (Entity) (Object) this;
            if (Conditional.shouldApplyPerspectiveTo(self)) {
                CreateTrainPerspectiveMod.INSTANCE.onEntityMountEvent(false, self, vehicle);
            }
        }
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 1, argsOnly = true)
    public float modifyPitch(float pitch, @Local(argsOnly = true, index = 2) float yaw) {
        if (this.level.isClientSide) {
            if (((Entity) (Object) this) instanceof Perspective persp
                    && persp.isEnabled()
                    && Conditional.shouldApplyPerspectiveTo((Entity) (Object) this)) {
                return MixinUtil.applyDirectionXRotChange(persp, pitch, yaw, 1.0f);
            } else return pitch;
        } else return pitch;
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(method = "calculateViewVector", at = @At(value = "LOAD"), index = 2, argsOnly = true)
    public float modifyYaw(float yaw, @Local(argsOnly = true, index = 1) float pitch) {
        if (this.level.isClientSide) {
            if (((Entity) (Object) this) instanceof Perspective persp
                    && persp.isEnabled()
                    && Conditional.shouldApplyPerspectiveTo((Entity) (Object) this)) {
                return yaw + MixinUtil.getExtraYRot(persp, pitch, yaw, 1.0f);
            } else return yaw;
        } else return yaw;
    }

    public void ctp$enable(CarriageContraptionEntity entity) {
        ctp$perspectiveActive = true;
        ctp$reference = entity;
    }

    public void ctp$disable() {
        ctp$perspectiveActive = false;
        ctp$reference = null;
    }

    public void ctp$setReference(CarriageContraptionEntity entity) {
        ctp$reference = entity;
        ctp$prevScale = ctp$scale;
        ctp$scale = 1.0f;
    }

    public CarriageContraptionEntity ctp$getReference() {
        return ctp$reference;
    }

    public void ctp$diminish() {
        if (ctp$scale <= 0.0f) return;
        ctp$prevScale = ctp$scale;
        ctp$scale -= 0.99f;
    }

    public float ctp$getScale() {
        return ctp$scale;
    }

    public float ctp$getPrevScale() {
        return ctp$prevScale;
    }

    public boolean ctp$isEnabled() {
        return ctp$perspectiveActive;
    }

    public @Nullable RotationState ctp$getRotationState() {
        return ctp$currentState;
    }

    public void ctp$setRotationState(@Nullable RotationState state) {
        ctp$currentState = state;
    }
}
