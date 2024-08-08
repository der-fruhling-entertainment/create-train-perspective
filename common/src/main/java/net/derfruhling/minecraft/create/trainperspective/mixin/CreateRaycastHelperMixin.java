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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.derfruhling.minecraft.create.trainperspective.Conditional;
import net.derfruhling.minecraft.create.trainperspective.MixinUtil;
import net.derfruhling.minecraft.create.trainperspective.Perspective;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RaycastHelper.class)
public class CreateRaycastHelperMixin {
    @ModifyReturnValue(method = "getTraceOrigin", at = @At("RETURN"))
    private static Vec3 applyLeaning(Vec3 original, Player player) {
        if (Conditional.shouldApplyPerspectiveTo(player) && player instanceof Perspective persp) {
            return MixinUtil.applyStandingCameraTranslation(player, original, persp, 1.0f);
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 4)
    private static float modifyPitch(float pitch, Player player) {
        if (player instanceof Perspective persp
                && Conditional.shouldApplyPerspectiveTo(player)) {
            return MixinUtil.applyDirectionXRotChange(persp, pitch, player.getYRot(), 1.0f);
        } else return pitch;
    }

    @ModifyVariable(method = "getTraceTarget", at = @At("STORE"), index = 5)
    private static float modifyYaw(float yaw, Player player) {
        if (player instanceof Perspective persp
                && Conditional.shouldApplyPerspectiveTo(player)) {
            return yaw + MixinUtil.getExtraYRot(persp, player.getXRot(), yaw, 1.0f);
        } else return yaw;
    }
}
