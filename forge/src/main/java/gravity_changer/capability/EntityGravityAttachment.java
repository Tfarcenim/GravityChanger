package gravity_changer.capability;

import gravity_changer.RotationAnimation;
import gravity_changer.api.RotationParameters;
import gravity_changer.util.GravityDirEffect;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public interface EntityGravityAttachment extends ICapabilitySerializable<CompoundTag> {

    void tick();
   RotationAnimation getRotationAnimation();
   void reset();

    double getBaseGravityStrength();

    void setBaseGravityStrength(double strength);

    Direction getCurrGravityDirection();

    double getCurrGravityStrength();

    Direction getPrevGravityDirection();

    Direction getBaseGravityDirection();

    void setBaseGravityDirection(Direction gravityDirection);

    void updateGravityStatus();

    void forceApplyGravityChange();

    void applyGravityDirectionEffect(
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    );

}
