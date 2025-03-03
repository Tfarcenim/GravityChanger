package gravity_changer.api;

import gravity_changer.capability.DimensionData;
import gravity_changer.capability.DimensionDataAttachment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public interface GravityChangerAPIForge {
    Capability<DimensionDataAttachment> DIMENSION_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    static LazyOptional<DimensionDataAttachment> getOptional(Level level) {
        return level.getCapability(DIMENSION_DATA);
    }

}
