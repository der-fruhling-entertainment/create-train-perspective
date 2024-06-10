package net.derfruhling.minecraft.create.trainperspective.mixin;

import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.derfruhling.minecraft.create.trainperspective.RotationState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayer.class)
@Implements({@Interface(iface = Perspective.class, prefix = "ctp$")})
@Environment(EnvType.CLIENT)
public class AbstractClientPlayerMixin {
    @Unique
    private boolean ctp$perspectiveActive = false;
    @Unique private float ctp$lean = 0.0f, ctp$yaw = 0.0f, ctp$oldLean = 0.0f, ctp$oldYaw = 0.0f;
    @Unique private @Nullable RotationState ctp$currentState = null;

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
