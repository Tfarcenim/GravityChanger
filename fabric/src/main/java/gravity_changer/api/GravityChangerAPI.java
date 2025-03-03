package gravity_changer.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import gravity_changer.DimensionGravityDataComponent;
import gravity_changer.GravityChangerComponents;
import gravity_changer.GravityComponent;
import gravity_changer.RotationAnimation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class GravityChangerAPI {
    public static final ComponentKey<GravityComponent> GRAVITY_COMPONENT =
        GravityChangerComponents.GRAVITY_COMP_KEY;
    
    public static final ComponentKey<DimensionGravityDataComponent> DIMENSION_DATA_COMPONENT =
        GravityChangerComponents.DIMENSION_COMP_KEY;


    @Nullable
    public static RotationAnimation getRotationAnimation(Entity entity) {
        return getGravityComponent(entity).getRotationAnimation();
    }

    public static GravityComponent getGravityComponent(Entity entity) {
        return GRAVITY_COMPONENT.get(entity);
    }

}
