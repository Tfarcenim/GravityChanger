package gravitychanger.api;

import gravitychanger.RotationAnimation;
import net.minecraft.core.Direction;

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
}