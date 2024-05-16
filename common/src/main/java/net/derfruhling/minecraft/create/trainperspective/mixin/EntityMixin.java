package net.derfruhling.minecraft.create.trainperspective.mixin;

import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public abstract class EntityMixin {
    @Shadow @Nullable private Entity vehicle;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "RETURN", ordinal = 4))
    public void onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        CreateTrainPerspectiveMod.INSTANCE.onEntityMount(true, (Entity)(Object)this, entity);
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    public void onRemoveVehicle(CallbackInfo ci) {
        if(vehicle != null) {
            CreateTrainPerspectiveMod.INSTANCE.onEntityMount(false, (Entity)(Object)this, vehicle);
        }
    }
}
