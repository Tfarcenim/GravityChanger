package gravitychanger.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import gravitychanger.DimensionGravityDataComponent;
import gravitychanger.GravityChangerComponents;
import gravitychanger.GravityComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class GravityChangerAPIFabric {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
        GravityChangerComponents.GRAVITY_COMP_KEY;
    
    public static final ComponentKey<DimensionGravityDataComponent> DIMENSION_DATA_COMPONENT =
        GravityChangerComponents.DIMENSION_COMP_KEY;


    public static GravityComponent getGravityComponent(Entity entity) {
        return GRAVITY_COMPONENT.get(entity);
    }

    public static DimensionGravityDataComponent getLevelGravityComponent(Level level) {
        return DIMENSION_DATA_COMPONENT.get(level);
    }
}
