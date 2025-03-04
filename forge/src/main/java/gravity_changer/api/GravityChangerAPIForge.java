package gravity_changer.api;

import gravity_changer.capability.DimensionAttachment;
import gravity_changer.capability.EntityGravityAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public interface GravityChangerAPIForge {
    Capability<DimensionAttachment> DIMENSION_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    Capability<EntityGravityAttachment> ENTITY_GRAVITY_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    static LazyOptional<DimensionAttachment> getDimensionAttachment(Level level) {
        return level.getCapability(DIMENSION_DATA);
    }

    static LazyOptional<EntityGravityAttachment> getEntityGravityAttachment(Entity entity) {
        return entity.getCapability(ENTITY_GRAVITY_DATA);
    }

}
