package gravitychanger.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface GravityChangerAPIForge {
    Capability<IEntityGravityData> ENTITY_GRAVITY = CapabilityManager.get(new CapabilityToken<>(){});
    Capability<ILevelGravityData> LEVEL_GRAVITY = CapabilityManager.get(new CapabilityToken<>(){});
}
