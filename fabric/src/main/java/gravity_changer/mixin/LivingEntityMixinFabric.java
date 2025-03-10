package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixinFabric extends Entity {

    public LivingEntityMixinFabric(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "baseTick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;",
                    ordinal = 0
            )
    )//todo forge
    private BlockPos redirect_baseTick_new_0(double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if (gravityDirection == Direction.DOWN) {
            return BlockPos.containing(x, y, z);
        }

        return BlockPos.containing(this.getEyePosition());
    }
}
