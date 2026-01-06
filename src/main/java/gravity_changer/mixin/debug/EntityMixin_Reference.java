package gravity_changer.mixin.debug;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin_Reference {

    @Shadow public abstract double getX();
    @Shadow public abstract Vec3d getEyePos();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow private World world;
    @Shadow public abstract Box getBoundingBox();

    @Shadow private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        return null;
    }

    @Shadow public abstract Vec3d getPos();
    @Shadow public abstract double getEyeY();
    @Shadow public abstract float getYaw(float tickDelta);
    @Shadow public abstract float getYaw();

    @Shadow public abstract World getWorld();

    @Shadow public abstract boolean isOnGround();

    @Shadow public abstract float getStepHeight();

    @Shadow
    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        throw new AssertionError();
    }

    @Shadow
    private static List<VoxelShape> findCollisionsForMovement(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox) {
        throw new AssertionError();
    }

    @Shadow
    private static float[] collectStepHeights(Box collisionBox, List<VoxelShape> collisions, float f, float stepHeight) {
        throw new AssertionError();
    }

    private Vec3d adjustMovementForCollisions_both(Vec3d movement) {
        Box boundingBox = this.getBoundingBox();
        List<VoxelShape> entityCollisions = this.getWorld().getEntityCollisions((Entity) (Object) this, boundingBox.stretch(movement));
        //ROTATE MOVEMENT, WORLD -> LOCAL ------------------------------------------------------------------------------
        Vec3d adjustedMovement = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions((Entity) (Object) this, movement, boundingBox, this.getWorld(), entityCollisions);

        //The adjusted movement being different means the bounding box collides with something
        boolean xCollides = movement.x != adjustedMovement.x;
        boolean yCollides = movement.y != adjustedMovement.y;
        boolean zCollides = movement.z != adjustedMovement.z;

        //Note, this could also be colliding downward with a shulker or boat, etc.
        boolean collidesWithGround = yCollides && movement.y < 0.0;

        //if you can step up, and are under the conditions to allow that
        //If you are grounded, have step height, and are moving horizontally, adjustedMovement is not enough,
        // and step height needs to be accounted for, because you MIGHT be able to step up.
        if (this.getStepHeight() > 0.0F && (collidesWithGround || this.isOnGround()) && (xCollides || zCollides)) {

            //This essentially snaps boundingBox to the ground (or whatever you
            //  are vertically colliding with in adjustedMovement)
            Box boxSnappedToGround = collidesWithGround ? boundingBox.offset(0.0, adjustedMovement.y, 0.0) : boundingBox;

            //snapped box stretched to your movement destination accounting for step height,
            // aka a region that accounts for everywhere your hitbox could end up
            Box movementBox = boxSnappedToGround.stretch(movement.x, this.getStepHeight(), movement.z);
            if (!collidesWithGround) {
                movementBox = movementBox.stretch(0.0, -1.0E-5F, 0.0);
            }

            //everything movementBox collides with (is overlapping, hasn't been moved out/adjusted)
            List<VoxelShape> allCollisions = findCollisionsForMovement((Entity) (Object) this, this.world, entityCollisions, movementBox);

            //remember boxSnappedToGround and movementBox have these applied if grounding is possible
            float distToGround = (float) adjustedMovement.y;
            float[] potentialStepHeights = collectStepHeights(boxSnappedToGround, allCollisions, this.getStepHeight(), distToGround);

            for (float exactStepHeight : potentialStepHeights) {

                //rotate this back
                Vec3d verticallyAdjustedMovement = adjustMovementForCollisions(new Vec3d(movement.x, exactStepHeight, movement.z), boxSnappedToGround, allCollisions);
                if (verticallyAdjustedMovement.horizontalLengthSquared() > adjustedMovement.horizontalLengthSquared()) {
                    double d = boundingBox.minY - boxSnappedToGround.minY;
                    return verticallyAdjustedMovement.add(0.0, -d, 0.0);
                }

            }
        }

        return adjustedMovement;
    }
}
