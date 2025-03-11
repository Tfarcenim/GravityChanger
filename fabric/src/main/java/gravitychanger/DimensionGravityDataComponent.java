package gravitychanger;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gravitychanger.api.ILevelGravityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class DimensionGravityDataComponent implements Component, AutoSyncedComponent, ILevelGravityData {
    double dimensionGravityStrength = 1;
    
    private final Level currentWorld;
    
    public DimensionGravityDataComponent(Level world) {
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
            GravityChangerComponents.DIMENSION_COMP_KEY.sync(currentWorld);
        }
    }
    
    @Override
    public void readFromNbt(CompoundTag tag) {
        dimensionGravityStrength = tag.getDouble("DimensionGravityStrength");
    }
    
    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putDouble("DimensionGravityStrength", dimensionGravityStrength);
    }
}
