package gravitychanger;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class DimensionGravityDataComponent implements Component, AutoSyncedComponent {
    double dimensionGravityStrength = 1;
    
    private final Level currentWorld;
    
    public DimensionGravityDataComponent(Level world) {
        this.currentWorld = world;
    }
    
    public double getDimensionGravityStrength() {
        return dimensionGravityStrength;
    }
    
    public void setDimensionGravityStrength(double strength) {
        if (!currentWorld.isClientSide) {
            dimensionGravityStrength = strength;
            GravityChangerComponents.DIMENSION_COMP_KEY.sync(currentWorld);
        }
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        dimensionGravityStrength = compoundTag.getDouble("DimensionGravityStrength");
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putDouble("DimensionGravityStrength", dimensionGravityStrength);
    }
}
