package gravity_changer.mixin.debug;

import gravity_changer.api.GravityChangerAPI;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

//NOT INCLUDED BY DEFAULT, IT'S ANNOYING, TODO: FIND A BETTER WAY
@Mixin(Entity.class)
public class EntityMixin_Debug {
    @Shadow private Vec3d pos;

    @Inject(method = "setPos", at = @At("HEAD"))
    private void debugOnSetPos(double x, double y, double z, CallbackInfo ci) {
        Entity this_ = (Entity) (Object) this;
        if (this_ instanceof ItemEntity) {
            String str = "%s ItemEntity#setPosRaw(%s, %s, %s) grav %s %s".formatted(
                this_.getWorld().isClient() ? "client" : "server", x, y, z,
                GravityChangerAPI.getGravityDirection(this_),
                GravityChangerAPI.getGravityStrength(this_)
            );
            System.out.println(str);
        }
    }


     /*private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
                    shift = At.Shift.BY, by = 2
            )

    )
    private void inject_test(CallbackInfoReturnable<Vec3d> cir,
                             @Local(ordinal = 0, argsOnly = true) Vec3d movement,
                             @Local(ordinal = 1) Vec3d adjustedMovement,
                             @Local(ordinal = 2) Vec3d verticallyAdjustedMovement
    ) {
        if (((Entity)(Object)this).getWorld().isClient()) {
            LOGGER.info("movement: {}", movement.toString());
            LOGGER.info("adjustedMovement: {}", adjustedMovement.toString());
            LOGGER.info("verticallyAdjustedMovement: {}", verticallyAdjustedMovement.toString());
        }
    }*/


    // the argument was transformed to local coord,
    // but this adjustMovementForCollisions needs world coord
    /*@ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private void redirect_adjustMovementForCollisions_vec_0(Args args) {
        Vec3d rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate);

        //if (((Entity)(Object)this).getWorld().isClient()) {
        //    LOGGER.info("vect: {}", args.get(0).toString());
        //}
    }*/


    /*@ModifyArgs(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private void redirect_adjustMovementForCollisions_add_0(Args args, @Local(ordinal=1) Vec3d adjustedMovement, @Local(ordinal=3) boolean collidesWithGround) {
        if (((Entity)(Object)this).getWorld().isClient()) {
            LOGGER.info("x: {}, y: {}, z: {}", args.get(0), args.get(1), args.get(2));
        }

        /*Vec3d rotate = new Vec3d(0.0, adjustedMovement.y, 0.0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }*/

    /*@Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;collectStepHeights(Lnet/minecraft/util/math/Box;Ljava/util/List;FF)[F",
                    ordinal = 0
            )
    )
    private float[] redirect_collectStepHeights(Box boxSnappedToGround, List<VoxelShape> allCollisions, float stepHeight, float distToGround) {
        FloatSet floatSet = new FloatArraySet(4);
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity)(Object)this);

        double relativeBottom = getRelativeBottom(boxSnappedToGround, gravityDirection);

        if(gravityDirection.getDirection() == Direction.AxisDirection.NEGATIVE) {
            for (VoxelShape voxelShape : allCollisions) {
                for (double collisionPoint : voxelShape.getPointPositions(gravityDirection.getAxis())) {
                    float verticalDist = (float)(collisionPoint - relativeBottom);

                    if (!(verticalDist < 0.0F) && verticalDist != distToGround) {
                        if (verticalDist > stepHeight) {
                            break;
                        }

                        floatSet.add(verticalDist);
                    }
                }
            }
        } else {
            for (VoxelShape voxelShape : allCollisions) {
                for (double collisionPoint : voxelShape.getPointPositions(gravityDirection.getAxis()).reversed()) {
                    float verticalDist = -(float)(collisionPoint - relativeBottom);

                    if (!(verticalDist < 0.0F) && verticalDist != distToGround) {
                        if (verticalDist > stepHeight) {
                            break;
                        }

                        floatSet.add(verticalDist);
                    }
                }
            }
        }


        float[] fs = floatSet.toFloatArray();

        //if (((Entity)(Object)this).getWorld().isClient()) {
        //    LOGGER.info(Arrays.toString(fs), this);
        //}

        FloatArrays.unstableSort(fs);
        return fs;
    }*/

}
