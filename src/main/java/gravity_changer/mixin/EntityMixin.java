package gravity_changer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import gravity_changer.GravityChangerMod;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

//@Debug(export = true)
@Mixin(Entity.class)
public abstract class EntityMixin {

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

    //TODO: Why does this exist? Might not be necessary anymore
    // and thus cause problems, check later
    @Inject(
        method = "calculateBoundingBox()Lnet/minecraft/util/math/Box;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inject_calculateBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof ProjectileEntity) return;
        
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Box box = cir.getReturnValue().offset(this.pos.negate());
        if (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(this.pos));
    }
    
    @Inject(
        method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inject_getRotationVector(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }
    
    @Inject(
        method = "getVelocityAffectingPos()Lnet/minecraft/util/math/BlockPos;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_getVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        cir.setReturnValue(BlockPos.ofFloored(this.pos.add(Vec3d.of(gravityDirection.getVector()).multiply(0.5000001D))));
    }
    
    @Inject(
        method = "getEyePos()Lnet/minecraft/util/math/Vec3d;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_getEyePos(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        cir.setReturnValue(RotationUtil.vecPlayerToWorld(0.0D, this.standingEyeHeight, 0.0D, gravityDirection).add(this.pos));
    }
    
    @Inject(
        method = "getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_getCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3d vec3d = RotationUtil.vecPlayerToWorld(0.0D, this.standingEyeHeight, 0.0D, gravityDirection);
        
        double d = MathHelper.lerp((double) tickDelta, this.prevX, this.getX()) + vec3d.x;
        double e = MathHelper.lerp((double) tickDelta, this.prevY, this.getY()) + vec3d.y;
        double f = MathHelper.lerp((double) tickDelta, this.prevZ, this.getZ()) + vec3d.z;
        cir.setReturnValue(new Vec3d(d, e, f));
    }
    
    @Inject(
        method = "getBrightnessAtEyes()F",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_getBrightnessAtFEyes(CallbackInfoReturnable<Float> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        cir.setReturnValue(this.world.isPosLoaded(this.getBlockX(), this.getBlockZ()) ? this.world.getBrightness(BlockPos.ofFloored(this.getEyePos())) : 0.0F);
    }
    
    // transform move vector from local to world (the velocity is local)
    @ModifyVariable(
        method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private Vec3d modify_move_Vec3d_0_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }
    
    // transform the argument vector back to local coordinate
    @ModifyVariable(
        method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            ordinal = 0
        ),
        ordinal = 0,
        argsOnly = true
    )
    private Vec3d modify_move_Vec3d_0_1(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    // transform the local variable (result from collide()) to local coordinate
    @ModifyVariable(
        method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            ordinal = 0
        ),
        ordinal = 1
    )
    private Vec3d modify_move_Vec3d_1(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    @Inject(
        method = "getLandingPos()Lnet/minecraft/util/math/BlockPos;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_getLandingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        BlockPos blockPos = BlockPos.ofFloored(RotationUtil.vecPlayerToWorld(0.0D, -0.20000000298023224D, 0.0D, gravityDirection).add(this.pos));
        cir.setReturnValue(blockPos);
    }

    //1.20.6 -> 1.21.1 - Unchanged
    // transform the argument to local coordinate
    @ModifyVariable(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(
                value = "INVOKE_ASSIGN",
                target = "Lnet/minecraft/world/World;getEntityCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;",
                ordinal = 0
        ),
        ordinal = 0,
        argsOnly = true
    )
    private Vec3d modify_adjustMovementForCollisions_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //1.20.6 -> 1.21.1 - Unchanged
    // transform the result to world coordinate
    // the input to Entity.adjustMovementForCollisions will be in local coord
    @Inject(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inject_adjustMovementForCollisions(CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    // the argument was transformed to local coord,
    // but bounding box stretch needs world coord
    @ModifyArgs(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
        )
    )
    private void redirect_adjustMovementForCollisions_stretch_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }

    // the argument was transformed to local coord,
    // but bounding box move needs world coord
    @ModifyArgs(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;"
        )
    )
    private void redirect_adjustMovementForCollisions_offset_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }

    // the argument was transformed to local coord,
    // but this adjustMovementForCollisions needs world coord
    @ModifyArgs(
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
    }

    //I know there is a better way to do this but I was unable to figure it out.
    // I've been working on this too long already and I don't think this will
    // be problematic for performance which is all I care about at this point
    @Redirect(
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

        FloatArrays.unstableSort(fs);
        return fs;
    }

    @Unique
    private static double getRelativeBottom(Box boxSnappedToGround, Direction gravityDirection) {
        double relativeBottom = boxSnappedToGround.minY;
        if(gravityDirection == Direction.DOWN)
            relativeBottom = boxSnappedToGround.minY;
        else if(gravityDirection == Direction.UP)
            relativeBottom = boxSnappedToGround.maxY;
        else if(gravityDirection == Direction.NORTH)
            relativeBottom = boxSnappedToGround.minZ;
        else if(gravityDirection == Direction.SOUTH)
            relativeBottom = boxSnappedToGround.maxZ;
        else if(gravityDirection == Direction.WEST)
            relativeBottom = boxSnappedToGround.minX;
        else if(gravityDirection == Direction.EAST)
            relativeBottom = boxSnappedToGround.maxX;
        return relativeBottom;
    }

    //1.20.6 -> 1.21.1 - Unchanged
    // Entity.collideBoundingBox is inputed with local coord, transform it to world coord
    @ModifyVariable(
        method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private static Vec3d modify_adjustMovementForCollisions_Vec3d_0(Vec3d vec3d, Entity entity) {
        if (entity == null) {
            return vec3d;
        }
        
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    //1.20.6 -> 1.21.1 - Unchanged
    //TODO: This changes WAY too much and is at risk of incompatibility with other mods and updates
    // however the last method like this I fixed took over an hour, so I'll leave it alone for now
    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        return redirection(movement, entityBoundingBox, collisions, (Entity) (Object) this);
    }

    @Redirect(
        method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        )
    )
    private static Vec3d redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions, Entity entity) {
        return redirection(movement, entityBoundingBox, collisions, entity);
    }

    @Unique
    private static Vec3d redirection(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions, Entity entity) {
        Direction gravityDirection;
        if (entity == null || (gravityDirection = GravityChangerAPI.getGravityDirection(entity)) == Direction.DOWN) {
            return adjustMovementForCollisions(movement, entityBoundingBox, collisions);
        }

        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        double playerMovementX = playerMovement.x;
        double playerMovementY = playerMovement.y;
        double playerMovementZ = playerMovement.z;
        Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
        Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
        Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
        if (playerMovementY != 0.0D) {
            playerMovementY = VoxelShapes.calculateMaxOffset(directionY.getAxis(), entityBoundingBox, collisions, playerMovementY * directionY.getDirection().offset()) * directionY.getDirection().offset();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = VoxelShapes.calculateMaxOffset(directionX.getAxis(), entityBoundingBox, collisions, playerMovementX * directionX.getDirection().offset()) * directionX.getDirection().offset();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
        }
        return new Vec3d(playerMovementX, playerMovementY, playerMovementZ);
        //return RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection);
    }
    
    @ModifyArgs(
        method = "isInsideWall",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;of(Lnet/minecraft/util/math/Vec3d;DDD)Lnet/minecraft/util/math/Box;",
            ordinal = 0
        )
    )
    private void modify_isInsideWall_of_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(1), args.get(2), args.get(3));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(1, rotate.x);
        args.set(2, rotate.y);
        args.set(3, rotate.z);
    }
    
    @ModifyArg(
        method = "getHorizontalFacing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Direction;fromRotation(D)Lnet/minecraft/util/math/Direction;"
        )
    )
    private double redirect_getHorizontalFacing_getYaw_0(double rotation) {
        Entity this_ = (Entity) (Object) this;
        
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this_);
        if (gravityDirection == Direction.DOWN) {
            return rotation;
        }
        
        return RotationUtil.rotPlayerToWorld((float) rotation, this.getPitch(), gravityDirection).x;
    }

    //TODO: I don't like this, but it was like this the previous version too,
    // I also don't want to touch mixin methods this size with a 10ft pole rn
    // if I don't have to
    @Inject(
        method = "spawnSprintingParticles()V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_spawnSprintingParticles(CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        ci.cancel();
        
        Vec3d floorPos = this.getPos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.20000000298023224D, 0.0D, gravityDirection));
        
        BlockPos blockPos = BlockPos.ofFloored(floorPos);
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d particlePos = this.getPos().add(RotationUtil.vecPlayerToWorld((this.random.nextDouble() - 0.5D) * (double) this.dimensions.width(), 0.1D, (this.random.nextDouble() - 0.5D) * (double) this.dimensions.width(), gravityDirection));
            Vec3d playerVelocity = this.getVelocity();
            Vec3d particleVelocity = RotationUtil.vecPlayerToWorld(playerVelocity.x * -4.0D, 1.5D, playerVelocity.z * -4.0D, gravityDirection);
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), particlePos.x, particlePos.y, particlePos.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
        }
    }

    //TODO: DOUBLE ORDINAL ISSUE AGAIN, ignored for now
    // probably used to be more than 1 method of same name,
    // will need to check earlier versions to know original purpose
    @ModifyVariable(
        method = "updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        ),
        ordinal = 1
    )
    private Vec3d modify_updateMovementInFluid_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }
    
    @ModifyArg(
        method = "updateMovementInFluid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;add(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 1
        ),
        index = 0
    )
    private Vec3d modify_updateMovementInFluid_add_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    //TODO: I don't like this, but it seems entirely unchanged, so its staying for now
    @Inject(
        method = "pushAwayFrom(Lnet/minecraft/entity/Entity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_pushAwayFrom(Entity entity, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        Direction otherGravityDirection = GravityChangerAPI.getGravityDirection(entity);
        
        if (gravityDirection == Direction.DOWN && otherGravityDirection == Direction.DOWN) return;
        
        ci.cancel();
        
        if (!this.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !this.noClip) {
                Vec3d entityOffset = entity.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter());
                
                {
                    Vec3d playerEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, gravityDirection);
                    double dx = playerEntityOffset.x;
                    double dz = playerEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if (f >= 0.01F) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }
                        
                        dx *= g;
                        dz *= g;
                        dx *= 0.05F;
                        dz *= 0.05F;
                        if (!this.hasPassengers()) {
                            this.addVelocity(-dx, 0.0D, -dz);
                        }
                    }
                }
                
                {
                    Vec3d entityEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, otherGravityDirection);
                    double dx = entityEntityOffset.x;
                    double dz = entityEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if (f >= 0.01F) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }
                        
                        dx *= g;
                        dz *= g;
                        dx *= 0.05F;
                        dz *= 0.05F;
                        if (!entity.hasPassengers()) {
                            entity.addVelocity(dx, 0.0D, dz);
                        }
                    }
                }
            }
        }
    }

    //TODO: I would prefer to have a starminer-esque space dimension above the world,
    // but thats out of scope for this mod and void damage is cool too,
    // I could not care less about horizontal void damage as implemented here,
    // but I can see it being cool in a custom dimension where distance from
    // 0,0,0 in ANY Axis gets treated the same (with different min and max y ofc),
    // which is again out of scope, however I'll be keeping both ideas in mind
    // with this 2do for a potential space mod
    // Maybe a mod that adds a way to survive the void (apart from god apple spam)
    // and once you get far enough into the void you enter a new dimension as
    // a progression check, kind of like a space dimension but exploring whatever the void is
    @Inject(
        method = "attemptTickInVoid()V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_attemptTickInVoid(CallbackInfo ci) {
        Entity this_ = (Entity) (Object) this;
    
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this_);
        if (GravityChangerMod.config.voidDamageAboveWorld &&
            this.getY() > (double) (this.world.getTopY() + 256) &&
            gravityDirection == Direction.UP
        ) {
            this.tickInVoid();
            ci.cancel();
            return;
        }
        
        if (GravityChangerMod.config.voidDamageOnHorizontalFallTooFar &&
            gravityDirection.getAxis() != Direction.Axis.Y &&
            fallDistance > 1024
            // TODO also handle reverse gravity strength
        ) {
            this.tickInVoid();
            ci.cancel();
            return;
        }
    }
    
    @ModifyArgs(
        method = "doesNotCollide(DDD)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;",
            ordinal = 0
        )
    )
    private void redirect_doesNotCollide_offset_0(Args args) {
        Vec3d rotate = new Vec3d(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }
    
    //Original method call does the same thing on current version, and since the getEyePos
    // method has a mixin to make it understand gravity, this is better, although it might be
    // best to do this differently later
    @ModifyVariable(
        method = "updateSubmergedInWaterState()V",
        at = @At(
            value = "STORE"
        ),
        ordinal = 0
    )
    private double submergedInWaterEyeFix(double d) {
        d = this.getEyePos().getY();
        return d;
    }
    
    @ModifyVariable(
        method = "updateSubmergedInWaterState()V",
        at = @At(
            value = "STORE"
        ),
        ordinal = 0
    )
    private BlockPos submergedInWaterPosFix(BlockPos blockpos) {
        blockpos = BlockPos.ofFloored(this.getEyePos());
        return blockpos;
    }

    //TODO: Verify this correctly implements gravity strength for most entities,
    // it is MUCH simpler than it was on previous versions
    @Inject(method = "getFinalGravity", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValue() * GravityChangerAPI.getGravityStrength(((Entity) (Object) this)));
    }
}
