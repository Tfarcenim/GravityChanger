package gravitychanger;

import gravitychanger.api.GravityUpdateEvent;
import gravitychanger.api.IEntityGravityData;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeEvents {
    static void init() {
        NeoForge.EVENT_BUS.addListener(NeoForgeEvents::updateGravityAnchor);
    }

    static void updateGravityAnchor(GravityUpdateEvent event) {
        Entity entity = event.getEntity();
        IEntityGravityData entityGravity = event.getGravity();
        CommonEvents.handleGravity(entity,entityGravity);
    }
}
