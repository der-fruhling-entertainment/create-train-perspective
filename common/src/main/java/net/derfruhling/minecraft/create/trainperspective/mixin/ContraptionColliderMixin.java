package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.derfruhling.minecraft.create.trainperspective.CreateTrainPerspectiveMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContraptionCollider.class)
@Environment(EnvType.CLIENT)
public class ContraptionColliderMixin {
    @Inject(method = "collideEntities", at = @At("HEAD"), remap = false)
    private static void saveClientPlayerFromClipping(
            AbstractContraptionEntity contraptionEntity,
            CallbackInfo ci
    ) {
        if(contraptionEntity instanceof CarriageContraptionEntity carriage) {
            CreateTrainPerspectiveMod.INSTANCE.tickStandingPlayers(carriage);
        }
    }
}
