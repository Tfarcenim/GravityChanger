package gravitychanger;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.network.S2CLevelGravityPacket;
import gravitychanger.platform.Services;
import gravitychanger.util.LevelGravityData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelGravityCapability extends LevelGravityData implements ICapabilitySerializable<CompoundTag> {
    public LevelGravityCapability(Level world) {
        super(world);
    }

    private final LazyOptional<ILevelGravityData> holder = LazyOptional.of(() -> this);

    @Override
    protected void syncToPlayers() {
        if (currentWorld instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                CompoundTag data = serializeNBT();
                Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return GravityChangerAPIForge.LEVEL_GRAVITY.orEmpty(cap,holder);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        toNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        fromNbt(nbt);
    }
}
