package gravity_changer.api;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class GravityUpdateEvent extends EntityEvent {
    public GravityUpdateEvent(Entity entity) {
        super(entity);
    }
}
