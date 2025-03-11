package gravitychanger.api;

import gravitychanger.RotationAnimation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IEntityGravityData {
    double getBaseGravityStrength();
    Direction getBaseGravityDirection();

    void setBaseGravityDirection(Direction gravityDirection);

    Direction getCurrGravityDirection();
    double getCurrGravityStrength();

    void setBaseGravityStrength(double strength);
    void reset();

    void updateGravityStatus();
    void forceApplyGravityChange();
    void applyGravityStrengthEffect(double strengthMultiplier);
    RotationAnimation getRotationAnimation();

    void applyGravityDirectionEffect(
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    );

    void toNbt(CompoundTag tag);
    void fromNbt(CompoundTag tag);
}