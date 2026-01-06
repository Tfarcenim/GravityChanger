package gravity_changer;

import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class DimensionGravityDataComponent implements Component, AutoSyncedComponent {
    double dimensionGravityStrength = 1;
    
    private final World currentWorld;
    
    public DimensionGravityDataComponent(World world) {
        this.currentWorld = world;
    }
    
    public double getDimensionGravityStrength() {
        return dimensionGravityStrength;
    }
    
    public void setDimensionGravityStrength(double strength) {
        if (!currentWorld.isClient) {
            dimensionGravityStrength = strength;
            GravityChangerComponents.DIMENSION_COMP_KEY.sync(currentWorld);
        }
    }
    
    /*@Override
    public void readFromNbt(NbtCompound tag) {
        dimensionGravityStrength = tag.getDouble("DimensionGravityStrength");
    }
    
    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("DimensionGravityStrength", dimensionGravityStrength);
    }*/

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        dimensionGravityStrength = nbtCompound.getDouble("DimensionGravityStrength");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putDouble("DimensionGravityStrength", dimensionGravityStrength);
    }
}
