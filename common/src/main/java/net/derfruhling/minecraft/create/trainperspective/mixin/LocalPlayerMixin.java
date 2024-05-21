package net.derfruhling.minecraft.create.trainperspective.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// If anything else tries to @Overwrite getViewYRot, use their changes.
@Mixin(value = LocalPlayer.class, priority = 200)
public class LocalPlayerMixin extends AbstractClientPlayer {
    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    /**
     * @author der_frühling
     * @reason Mojang, for some reason, is not using the value of getViewYRot unless
     * the player is a passenger.
     * This breaks yaw while standing on a train.
     * This is an {@link Overwrite @Overwrite} method because of the small
     * size of the target method and the simple fix it needs to apply.
     */
    @Overwrite
    public float getViewYRot(float f) {
        return super.getViewYRot(f);
    }
}
