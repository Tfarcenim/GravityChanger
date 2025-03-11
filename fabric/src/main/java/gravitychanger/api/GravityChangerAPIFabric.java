package gravitychanger.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import gravitychanger.DimensionGravityDataComponent;
import gravitychanger.EntityTags;
import gravitychanger.GravityChangerComponents;
import gravitychanger.GravityComponent;
import gravitychanger.RotationAnimation;
import gravitychanger.util.RotationUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class GravityChangerAPIFabric {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
        GravityChangerComponents.GRAVITY_COMP_KEY;
    
    public static final ComponentKey<DimensionGravityDataComponent> DIMENSION_DATA_COMPONENT =
        GravityChangerComponents.DIMENSION_COMP_KEY;


    public static double getDimensionGravityStrength(Level world) {
        return DIMENSION_DATA_COMPONENT.get(world).getDimensionGravityStrength();
    }
    
    public static void setDimensionGravityStrength(Level world, double strength) {
        DIMENSION_DATA_COMPONENT.get(world).setDimensionGravityStrength(strength);
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public static RotationAnimation getRotationAnimation(Entity entity) {
        return getGravityComponent(entity).getRotationAnimation();
    }

    public static GravityComponent getGravityComponent(Entity entity) {
        return GRAVITY_COMPONENT.get(entity);
    }
    
    /**
     * Returns the world relative velocity for the given entity
     * Using minecraft's methods to get the velocity will return entity local velocity
     */
    public static Vec3 getWorldVelocity(Entity entity) {
        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), GravityChangerAPI.getGravityDirection(entity));
    }
    
    /**
     * Sets the world relative velocity for the given player
     * Using minecraft's methods to set the velocity of an entity will set player relative velocity
     */
    public static void setWorldVelocity(Entity entity, Vec3 worldVelocity) {
        entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(worldVelocity, GravityChangerAPI.getGravityDirection(entity)));
    }
    
    /**
     * Returns eye position offset from feet position for the given entity
     */
    public static Vec3 getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0, (double) entity.getEyeHeight(), 0, GravityChangerAPI.getGravityDirection(entity));
    }
    
    public static boolean canChangeGravity(Entity entity) {
        return EntityTags.canChangeGravity(entity);
    }
    
}
