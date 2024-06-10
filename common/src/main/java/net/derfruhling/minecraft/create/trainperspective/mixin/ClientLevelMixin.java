package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    public void onTickNonPassenger(Entity entity, CallbackInfo ci) {
        if (Conditional.shouldApplyPerspectiveTo(entity)) {
            CreateTrainPerspectiveMod.INSTANCE.tickEntity(entity, (Perspective) entity);
        }
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER))
    public void onTickPassenger(Entity vehicle, Entity rider, CallbackInfo ci) {
        if (Conditional.shouldApplyPerspectiveTo(rider) && vehicle instanceof CarriageContraptionEntity) {
            CreateTrainPerspectiveMod.INSTANCE.tickEntity(rider, (Perspective) rider);
        }
    }
}
