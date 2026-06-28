package gravitychanger.api;

import gravitychanger.EntityDuck;
import gravitychanger.EntityTags;
import gravitychanger.RotationAnimation;
import gravitychanger.attachments.DataAttachmentUtil;
import gravitychanger.util.EntityGravityData;
import gravitychanger.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public interface GravityChangerAPI {

    static EntityGravityData getGravityData(Entity entity) {
        return ((EntityDuck)entity).getGravityData();
    }

    static double getBaseGravityStrength(Entity entity) {
        return getGravityData(entity).getBaseGravityStrength();
    }
    /**
     * Returns the applied gravity direction for the given entity
     */
    static Direction getGravityDirection(Entity entity) {
        IEntityGravityData data = getGravityData(entity);
        return data == null ? Direction.DOWN : data.getCurrGravityDirection();
        //return getGravityComponent(entity).getCurrGravityDirection();
    }

    static double getGravityStrength(Entity entity) {
        IEntityGravityData data = getGravityData(entity);
        return data == null ? 1 : data.getCurrGravityStrength();
    }

    static void setBaseGravityDirection(
            Entity entity, Direction gravityDirection
    ) {
        IEntityGravityData component = getGravityData(entity);
        component.setBaseGravityDirection(gravityDirection);
    }

    /**
     * Returns the main gravity direction for the given entity
     * This may not be the applied gravity direction for the player, see GravityChangerAPIFabric#getAppliedGravityDirection
     */
    static Direction getBaseGravityDirection(Entity entity) {
        return getGravityData(entity).getBaseGravityDirection();
    }

    static void setBaseGravityStrength(Entity entity, double strength) {
        IEntityGravityData component = getGravityData(entity);
        component.setBaseGravityStrength(strength);
    }

    static void resetGravity(Entity entity) {
        if (!EntityTags.canChangeGravity(entity)) {return;}

        getGravityData(entity).reset();
    }

    /**
     * Instantly set gravity direction on client side without performing animation.
     * Not needed in normal cases.
     * (Used by ImmPtl)
     */
    static void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {
        Validate.isTrue(entity.level().isClientSide(), "should only be used on client");

        IEntityGravityData component = getGravityData(entity);

        component.setBaseGravityDirection(direction);

        component.updateGravityStatus(false);

        component.forceApplyGravityChange();
    }

    @Nullable
    static RotationAnimation getRotationAnimation(Entity entity) {
        IEntityGravityData data = getGravityData(entity);
        return data == null ? null :data.getRotationAnimation();
    }

    static double getDimensionGravityStrength(Level world) {
        return DataAttachmentUtil.getLevelGravityData(world);
    }

    static void setDimensionGravityStrength(Level world, double strength) {
        DataAttachmentUtil.setLevelGravityData(world,strength);
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

    static boolean canChangeGravity(Entity entity) {
        return EntityTags.canChangeGravity(entity);
    }
}
