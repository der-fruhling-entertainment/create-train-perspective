package net.derfruhling.minecraft.create.trainperspective.fabric.mixin;

import net.derfruhling.minecraft.create.trainperspective.fabric.ModFabricEntrypoint;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow @Nullable private Entity vehicle;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "RETURN", ordinal = 4))
    public void onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        ModFabricEntrypoint.getInstance().common.onEntityMount(true, (Entity)(Object)this, entity);
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    public void onRemoveVehicle(CallbackInfo ci) {
        if(vehicle != null) {
            ModFabricEntrypoint.getInstance().common.onEntityMount(false, (Entity)(Object)this, vehicle);
        }
    }
}
