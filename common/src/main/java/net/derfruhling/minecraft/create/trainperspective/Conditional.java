package net.derfruhling.minecraft.create.trainperspective;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class Conditional {
    private Conditional() {
    }

    public static boolean shouldApplyPerspectiveTo(Entity player) {
        if (ModConfig.INSTANCE.enabled) {
            return (ModConfig.INSTANCE.applyToNonPlayerEntities && !ModConfig.INSTANCE.blockedEntities.contains(EntityType.getKey(player.getType()))) ||
                    (ModConfig.INSTANCE.applyToOthers && player instanceof RemotePlayer)
                    || player instanceof LocalPlayer;
        } else {
            return false;
        }
    }

    public static boolean shouldApplyLeaning() {
        return ModConfig.INSTANCE.leanEnabled;
    }

    public static boolean shouldApplyRolling() {
        return ModConfig.INSTANCE.rollEnabled;
    }
}