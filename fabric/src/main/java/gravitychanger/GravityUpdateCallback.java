package gravitychanger;

import gravitychanger.util.EntityGravityData;
import net.minecraft.world.entity.Entity;

public interface GravityUpdateCallback {
    void update(Entity entity, EntityGravityData component);
}
