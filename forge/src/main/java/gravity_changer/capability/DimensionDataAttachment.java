package gravity_changer.capability;

import net.minecraftforge.common.util.INBTSerializable;

public interface DimensionDataAttachment {
    double getDimensionGravityStrength();
    void setDimensionGravityStrength(double dimensionGravityStrength);
}
