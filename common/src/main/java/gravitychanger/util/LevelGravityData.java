package gravitychanger.util;

import gravitychanger.api.ILevelGravityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class LevelGravityData implements ILevelGravityData {
    protected final Level currentWorld;
    protected double dimensionGravityStrength = 1;

    public LevelGravityData(Level world) {
        this.currentWorld = world;
    }

    @Override
    public double getDimensionGravityStrength() {
        return dimensionGravityStrength;
    }

    @Override
    public void setDimensionGravityStrength(double strength) {
        if (!currentWorld.isClientSide) {
            dimensionGravityStrength = strength;
            syncToPlayers();
        }
    }
    protected abstract void syncToPlayers();

    public void fromNbt(CompoundTag tag) {
        dimensionGravityStrength = tag.getDouble("DimensionGravityStrength");
    }

    public void toNbt(CompoundTag tag) {
        tag.putDouble("DimensionGravityStrength", dimensionGravityStrength);
    }

}
