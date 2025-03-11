package gravitychanger.api;

import net.minecraft.nbt.CompoundTag;

public interface ILevelGravityData {
     double getDimensionGravityStrength();
     void setDimensionGravityStrength(double strength);

     void toNbt(CompoundTag tag);
     void fromNbt(CompoundTag tag);
}
