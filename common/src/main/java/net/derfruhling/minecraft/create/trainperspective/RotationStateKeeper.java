package net.derfruhling.minecraft.create.trainperspective;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.jetbrains.annotations.Nullable;

public interface RotationStateKeeper {
    @Nullable CarriageContraptionEntity getContraption();
    void setCarriageEntity(@Nullable CarriageContraptionEntity entity);
    boolean isStanding();
    boolean isMounted();
    boolean shouldTickState();
    void onMounted();
    void onDismount();
    void setShouldTickState(boolean value);
    int getTicksSinceLastUpdate();
    void update();
    void tick();
    float getYawDelta();
}