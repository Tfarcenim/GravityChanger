package gravitychanger.api;

import net.minecraft.core.Direction;

public interface IGravityData {
    void setBaseGravityDirection(Direction gravityDirection);
    Direction getCurrGravityDirection();
    double getCurrGravityStrength();
}
