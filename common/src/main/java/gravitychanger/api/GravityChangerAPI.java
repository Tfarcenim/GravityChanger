package gravitychanger.api;

import gravitychanger.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public interface GravityChangerAPI {

    static IGravityData getGravityData(Entity entity) {
        return Services.PLATFORM.getGravityData(entity);
    }


    /**
     * Returns the applied gravity direction for the given entity
     */
    static Direction getGravityDirection(Entity entity) {
        return getGravityData(entity).getCurrGravityDirection();
        //return getGravityComponent(entity).getCurrGravityDirection();
    }

    static double getGravityStrength(Entity entity) {
        return getGravityData(entity).getCurrGravityStrength();
    }

    static void setBaseGravityDirection(
            Entity entity, Direction gravityDirection
    ) {
        IGravityData component = getGravityData(entity);
        component.setBaseGravityDirection(gravityDirection);
    }
}
