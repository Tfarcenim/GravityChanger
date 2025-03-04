package gravity_changer.capability;

import gravity_changer.RotationAnimation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

@AutoRegisterCapability
public interface EntityGravityAttachment extends ICapabilitySerializable<CompoundTag> {

    void tick();
   RotationAnimation getRotationAnimation();
   void reset();

}
