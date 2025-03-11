package gravitychanger.api;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class GravityUpdateEvent extends EntityEvent {
    private final IEntityGravityData gravityData;

    public GravityUpdateEvent(Entity entity, IEntityGravityData gravityData) {
        super(entity);
        this.gravityData = gravityData;
    }

    public IEntityGravityData getGravity() {
        return gravityData;
    }
}
