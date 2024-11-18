/*
 * Part of the Create: Train Perspective project.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 der_frühling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.derfruhling.minecraft.create.trainperspective.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

// workaround for figura to work (this is terrible but works)
@Mixin(value = Camera.class, priority = 1100)
@Implements({@Interface(iface = Camera3D.class, prefix = "c3d$")})
@Environment(EnvType.CLIENT)
public abstract class CameraMixin {
    @Shadow
    private Entity entity;
    @Unique
    private float ctp$zRot;
    @Unique
    private float ctp$extraYRot;
    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    protected abstract void setRotation(float f, float g);

    @Shadow
    protected abstract void setPosition(double d, double e, double f);

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", remap = false), index = 2)
    private float modifyRoll(float original) {
        return original + (ctp$zRot * Mth.DEG_TO_RAD);
    }

    @Unique
    public float c3d$getZRot() {
        return this.ctp$zRot;
    }

    @Unique
    public float c3d$getExtraYRot() {
        return this.ctp$extraYRot;
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void modifyRotations(Camera instance,
                                float y,
                                float x,
                                BlockGetter blockGetter,
                                Entity entity,
                                boolean isThirdPerson,
                                boolean bl2,
                                float f) {
        if (entity instanceof Perspective persp
                && Conditional.shouldApplyPerspectiveTo(entity)
                && Conditional.shouldApplyLeaning()
                && !isThirdPerson) {
            if (ModConfig.INSTANCE.debugEnableYawLock) y = ModConfig.INSTANCE.debugYawLock;

            if (Conditional.shouldApplyRolling()) {
                ctp$zRot = persp.getLean(f)
                        * ModConfig.INSTANCE.rollMagnitude
                        * Mth.cos((persp.getYaw(f) - y) * Mth.DEG_TO_RAD)
                        * Mth.cos(x * Mth.DEG_TO_RAD);
            }

            ctp$extraYRot = MixinUtil.getExtraYRot(persp, x, y, f);
            var newX = MixinUtil.applyDirectionXRotChange(persp, x, y, f);

            if(ModConfig.INSTANCE.debugMode == DebugMode.SHOW_CAMERA_ROTATION) {
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.displayClientMessage(Component.literal(String.format(
                        "%.03f, %.03f (%.03f), %.03f",
                        newX,
                        y,
                        ctp$extraYRot,
                        ctp$zRot
                )), true);
            }

            setRotation(
                    y,
                    newX
            );
        } else {
            ctp$zRot = 0;
            ctp$extraYRot = 0;
            setRotation(y, x);
        }
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    public void modifyPosition(Camera instance,
                               double x,
                               double y,
                               double z,
                               BlockGetter blockGetter,
                               Entity entity,
                               boolean isThirdPerson,
                               boolean bl2,
                               float f) {
        if (entity instanceof AbstractClientPlayer clientPlayer
                && Conditional.shouldApplyPerspectiveTo(entity)
                && Conditional.shouldApplyLeaning()
//                && clientPlayer.getVehicle() == null
                && !isThirdPerson) {
            var persp = (Perspective) clientPlayer;
            var newV = MixinUtil.applyStandingCameraTranslation(clientPlayer, x, y, z, persp, f);

            if(entity.isPassenger()) newV.y += 0.5;

            if (ModConfig.INSTANCE.debugMode == DebugMode.SHOW_STANDING_TRANSFORMS) {
                clientPlayer.displayClientMessage(Component.literal("%f, %f, %f".formatted(x - newV.x, y - newV.y, z - newV.z)), true);
            }

            setPosition(newV.x, newV.y, newV.z);
        } else {
            setPosition(x, y, z);
        }
    }
}
