package gravitychanger;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gravitychanger.util.LevelGravityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class DimensionGravityDataComponent extends LevelGravityData implements Component, AutoSyncedComponent {

    public DimensionGravityDataComponent(Level world) {
        super(world);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        fromNbt(tag);
    }
    
    @Override
    public void writeToNbt(CompoundTag tag) {
        toNbt(tag);
    }

    @Override
    protected void syncToPlayers() {
        GravityChangerComponents.DIMENSION_COMP_KEY.sync(currentWorld);
    }
}
