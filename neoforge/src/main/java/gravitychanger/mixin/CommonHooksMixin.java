package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommonHooks.class)
public class CommonHooksMixin {
    @Redirect(
            method = "onLivingBreathe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;",
                    ordinal = 0
            )
    )
    private static BlockPos redirect_baseTick_new_0(double x, double y, double z, LivingEntity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return BlockPos.containing(x, y, z);
        }
        return BlockPos.containing(entity.getEyePosition());
    }
}
