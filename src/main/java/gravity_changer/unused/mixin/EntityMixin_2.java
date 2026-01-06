package gravity_changer.unused.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin_2 {

    @Shadow private Vec3d pos;
    @Shadow private EntityDimensions dimensions;
    @Shadow private float standingEyeHeight;
    @Shadow public double prevX;
    @Shadow public double prevY;
    @Shadow public double prevZ;
    @Shadow public abstract double getX();
    @Shadow public abstract Vec3d getEyePos();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow private World world;
    @Shadow public abstract int getBlockX();
    @Shadow public abstract int getBlockZ();
    @Shadow public boolean noClip;
    @Shadow public abstract Vec3d getVelocity();
    @Shadow public abstract boolean hasPassengers();
    @Shadow public abstract Box getBoundingBox();

    @Shadow private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        return null;
    }

    @Shadow public abstract Vec3d getPos();
    @Shadow public abstract boolean isConnectedThroughVehicle(Entity entity);
    @Shadow public abstract void addVelocity(double deltaX, double deltaY, double deltaZ);
    @Shadow protected abstract void tickInVoid();
    @Shadow public abstract double getEyeY();
    @Shadow public abstract float getYaw(float tickDelta);
    @Shadow public abstract float getYaw();
    @Shadow public abstract float getPitch();

    @Shadow @Final protected Random random;

    @Shadow public float fallDistance;

    // looks like not useful
//    @ModifyArg(
//        method = "move",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/util/math/Vec3d;multiply(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
//            ordinal = 0
//        ),
//        index = 0
//    )
//    private Vec3 modify_move_multiply_0(Vec3 vec3d) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
//        if (gravityDirection == Direction.DOWN) {
//            return vec3d;
//        }
//
//        return RotationUtil.maskPlayerToWorld(vec3d, gravityDirection);
//    }
}
