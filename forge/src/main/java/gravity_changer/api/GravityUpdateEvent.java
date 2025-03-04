package gravity_changer.api;

import gravity_changer.capability.EntityGravity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class GravityUpdateEvent extends EntityEvent {
    public GravityUpdateEvent(Entity entity, EntityGravity entityGravity) {
        super(entity);
    }
}
