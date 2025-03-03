package gravity_changer.capability;

import gravity_changer.api.GravityChangerAPIForge;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionData implements  ICapabilitySerializable<CompoundTag>,DimensionDataAttachment {

    private final LazyOptional<DimensionDataAttachment> holder = LazyOptional.of(() -> this);

    double dimensionGravityStrength = 1;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return GravityChangerAPIForge.DIMENSION_DATA.orEmpty(cap,holder);
    }

    @Override
    public double getDimensionGravityStrength() {
        return dimensionGravityStrength;
    }

    @Override
    public void setDimensionGravityStrength(double dimensionGravityStrength) {
        this.dimensionGravityStrength = dimensionGravityStrength;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("dimension_gravity_strength",dimensionGravityStrength);
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        dimensionGravityStrength = nbt.getDouble("dimension_gravity_strength");
    }
}
