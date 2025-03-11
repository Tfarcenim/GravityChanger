package gravitychanger;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.util.EntityGravityData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityGravityCapability extends EntityGravityData implements ICapabilitySerializable<CompoundTag> {
    public EntityGravityCapability(Entity entity) {
        super(entity);
    }
    private final LazyOptional<IEntityGravityData> holder = LazyOptional.of(() -> this);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return GravityChangerAPIForge.ENTITY_GRAVITY.orEmpty(capability,holder);
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {

    }
////////////////
    @Override
    protected void syncEntity() {

    }

    @Override
    protected void sendSyncPacketToOtherPlayers() {

    }

    @Override
    protected void postEvent() {

    }
}
