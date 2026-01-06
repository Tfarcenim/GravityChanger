package gravity_changer.mixin;


import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//TODO: Verify this works
@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {
    @Redirect(
        method = "moveEntity(Lnet/minecraft/util/math/Direction;Lnet/minecraft/entity/Entity;DLnet/minecraft/util/math/Direction;)V",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        )
    )
    private static Vec3d redirect_moveEntity_Vec3d_0(double x, double y, double z, Direction direction, Entity entity, double d, Direction direction2) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }
        
        return RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection);
    }
}
