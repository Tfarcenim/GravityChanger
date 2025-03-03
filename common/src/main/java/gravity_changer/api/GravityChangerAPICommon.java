package gravity_changer.api;

import gravity_changer.EntityTags;
import gravity_changer.platform.Services;
import gravity_changer.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public interface GravityChangerAPICommon {
    /**
     * Returns the applied gravity direction for the given entity
     */
    static Direction getGravityDirection(Entity entity) {
        return Services.PLATFORM.getGravityDirection(entity);
    }

    static double getGravityStrength(Entity entity) {
        return Services.PLATFORM.getGravityStrength(entity);
    }

    static double getBaseGravityStrength(Entity entity) {
        return Services.PLATFORM.getBaseGravityStrength(entity);
    }

    static void setBaseGravityStrength(Entity entity, double strength) {
        Services.PLATFORM.setBaseGravityStrength(entity, strength);
    }

    static boolean canChangeGravity(Entity entity) {
        return EntityTags.canChangeGravity(entity);
    }

    /**
     * Returns the world relative velocity for the given entity
     * Using minecraft's methods to get the velocity will return entity local velocity
     */
    static Vec3 getWorldVelocity(Entity entity) {
        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), getGravityDirection(entity));
    }

    /**
     * Sets the world relative velocity for the given player
     * Using minecraft's methods to set the velocity of an entity will set player relative velocity
     */
    static void setWorldVelocity(Entity entity, Vec3 worldVelocity) {
        entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(worldVelocity, getGravityDirection(entity)));
    }

    /**
     * Returns eye position offset from feet position for the given entity
     */
    static Vec3 getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0, (double) entity.getEyeHeight(), 0, getGravityDirection(entity));
    }

    /**
     * Instantly set gravity direction on client side without performing animation.
     * Not needed in normal cases.
     * (Used by ImmPtl)
     */
    static void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {
        Validate.isTrue(entity.level().isClientSide(), "should only be used on client");
        Services.PLATFORM.instantlySetClientBaseGravityDirection(entity, direction);
    }

    static double getDimensionGravityStrength(Level world) {
        return Services.PLATFORM.getLevelGravity(world);
    }

    static void setDimensionGravityStrength(Level world, double strength) {
        Services.PLATFORM.setLevelGravity(world,strength);
    }
}
