package gravity_changer.capability;

import gravity_changer.RotationAnimation;
import gravity_changer.api.GravityChangerAPIForge;
import gravity_changer.util.GCUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityGravity  implements ICapabilitySerializable<CompoundTag>,EntityGravityAttachment{

    private final Entity entity;
    private final LazyOptional<EntityGravityAttachment> holder = LazyOptional.of(() -> this);

    // Only used on client, not synchronized.
    @Nullable
    public final RotationAnimation animation;

    private boolean isFiringUpdateEvent = false;

    public EntityGravity(Entity entity) {
        this.entity = entity;
        animation = entity.level().isClientSide() ? new RotationAnimation() : null;
    }

    @Override
    public void tick() {
        updateGravityStatus();

        applyGravityChange();
    }

    private void applyGravityChange() {

    }

    private void updateGravityStatus() {
        // for the remote players and non-player entities,
        // their effect data is not synchronized to the client
        // (possibly for making it harder to cheat for hacked clients)
        // then we don't calculate its gravity in normal way in client
        if (shouldAcceptServerSync()) {
            return;
        }
    }

    private boolean shouldAcceptServerSync() {
        return entity.level().isClientSide() && !GCUtil.isClientPlayer(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return GravityChangerAPIForge.ENTITY_GRAVITY_DATA.orEmpty(cap,holder);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
