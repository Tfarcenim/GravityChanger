package gravity_changer.api;

import gravity_changer.capability.EntityGravity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class GravityUpdateEvent extends EntityEvent {
    private final EntityGravity gravity;

    public GravityUpdateEvent(Entity entity, EntityGravity gravity) {
        super(entity);
        this.gravity = gravity;
    }

    public EntityGravity getGravity() {
        return gravity;
    }
}
