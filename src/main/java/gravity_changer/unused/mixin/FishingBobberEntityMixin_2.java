package gravity_changer.unused.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin_2 extends Entity {

    public FishingBobberEntityMixin_2(EntityType<?> type, World world) { super(type, world); }

    // TODO fishing hook
//    @WrapOperation(
//        method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPositionAndAngles(DDDFF)V",
//            ordinal = 0
//        )
//    )
//    private void wrapOperation_init_(FishingHook fishingBobberEntity, double x, double y, double z, float yaw, float pitch, Operation<Void> original, Player thrower, Level world, int lureLevel, int luckOfTheSeaLevel) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
//        if(gravityDirection == Direction.DOWN) {
//            original.call(fishingBobberEntity, x, y, z, yaw, pitch);
//            return;
//        }
//
//        Vec3 pos = thrower.getEyePos();
//        Vec2 rot = RotationUtil.rotPlayerToWorld(yaw, pitch, gravityDirection);
//        original.call(fishingBobberEntity, pos.x, pos.y, pos.z, rot.x, rot.y);
//    }
//
//    @ModifyVariable(
//        method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
//        at = @At(
//            value = "INVOKE_ASSIGN",
//            target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;",
//            ordinal = 0
//        ),
//        ordinal = 0
//    )
//    private Vec3 modify_init_Vec3d_1(Vec3 vec3d, Player thrower, Level world, int lureLevel, int luckOfTheSeaLevel) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
//        if(gravityDirection == Direction.DOWN) {
//            return vec3d;
//        }
//
//        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
//    }

}
