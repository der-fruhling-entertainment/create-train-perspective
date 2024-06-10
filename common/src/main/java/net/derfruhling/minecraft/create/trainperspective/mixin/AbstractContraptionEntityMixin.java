package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContraptionEntity.class)
@Environment(EnvType.CLIENT)
public class AbstractContraptionEntityMixin {
    @SuppressWarnings({"ConstantValue", "UnreachableCode"})
    @Inject(method = "registerColliding", at = @At("TAIL"), remap = false)
    private void onRegisterColliding(
            Entity entity,
            CallbackInfo ci
    ) {
        if (!entity.level().isClientSide) return;
        if ((Object) this instanceof CarriageContraptionEntity carriage
                && entity instanceof Perspective
                && Conditional.shouldApplyPerspectiveTo(entity)) {
            CreateTrainPerspectiveMod.INSTANCE.tickStandingEntity(carriage, entity);
        }
    }
}
