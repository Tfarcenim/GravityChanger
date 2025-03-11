package gravitychanger.util;

import com.mojang.logging.LogUtils;
import gravitychanger.RotationAnimation;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.RotationParameters;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class EntityGravityData implements IEntityGravityData {

    protected static final Logger LOGGER = LogUtils.getLogger();
    public final Entity entity;
    // Only used on client, not synchronized.
    @Nullable
    public final RotationAnimation animation;
    // if it equals entity.tickCount,
    // it means that the gravity update event has already fired in this tick
    protected long lastUpdateTickCount = 0;

    // only used on server side
    protected boolean needsSync = false;

    protected boolean initialized = false;
    // not synchronized
    protected Direction prevGravityDirection = Direction.DOWN;
    protected double prevGravityStrength = 1.0;
    protected Direction currGravityDirection = Direction.DOWN;
    protected double currGravityStrength = 1.0;
    protected double currentEffectPriority = Double.MIN_VALUE;
    protected boolean isFiringUpdateEvent = false;
    @Nullable
    protected EntityGravityData.GravityDirEffect delayApplyDirEffect = null;
    protected double delayApplyStrengthEffect = 1.0;
    // the base gravity direction
    protected Direction baseGravityDirection = Direction.DOWN;
    // the base gravity strength
    protected double baseGravityStrength = 1.0;
    @Nullable
    protected RotationParameters currentRotationParameters = RotationParameters.getDefault();

    public EntityGravityData(Entity entity) {
        this.entity = entity;
        if (entity.level().isClientSide()) {
            animation = new RotationAnimation();
        }
        else {
            animation = null;
        }
    }

    public record GravityDirEffect(
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    ) {

    }
}
