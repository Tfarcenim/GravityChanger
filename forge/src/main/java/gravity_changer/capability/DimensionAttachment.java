package gravity_changer.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface DimensionAttachment {
    double getDimensionGravityStrength();
    void setDimensionGravityStrength(double dimensionGravityStrength);
}
