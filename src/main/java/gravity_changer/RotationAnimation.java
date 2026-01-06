package gravity_changer;

import gravity_changer.util.QuaternionUtil;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotationAnimation {
    private boolean inAnimation = false;
    private Quaternionf startGravityRotation;
    private Quaternionf endGravityRotation;
    private Vec3d relativeRotationCenter = Vec3d.ZERO;
    
    private long startTimeMs;
    private long endTimeMs;
    
    public void startRotationAnimation(
        Direction newGravity, Direction prevGravity,
        long durationTimeMs, Entity entity, long timeMs,
        boolean rotateView, Vec3d relativeRotationCenter
    ) {
        if (durationTimeMs == 0) {
            inAnimation = false;
            return;
        }
        
        Validate.notNull(entity);
        
        Vec3d newLookingDirection = getNewLookingDirection(newGravity, prevGravity, entity, rotateView);
        
        Quaternionf oldViewRotation = QuaternionUtil.getViewRotation(entity.getPitch(), entity.getYaw());
        
        update(timeMs);
        Quaternionf currentAnimatedGravityRotation = getCurrentGravityRotation(prevGravity, timeMs);
        
        // camera rotation = view rotation(pitch and yaw) * gravity rotation(animated)
        Quaternionf currentAnimatedCameraRotation = new Quaternionf().set(oldViewRotation).mul(currentAnimatedGravityRotation);
        
        Quaternionf newEndGravityRotation = RotationUtil.getWorldRotationQuaternion(newGravity);
        
        Vec2f newYawAndPitch = RotationUtil.vecToRot(
            RotationUtil.vecWorldToPlayer(newLookingDirection, newGravity)
        );
        float newPitch = newYawAndPitch.y;
        float newYaw = newYawAndPitch.x;
        float deltaYaw = newYaw - entity.getYaw();
        float deltaPitch = newPitch - entity.getPitch();
        entity.setYaw(entity.getYaw() + deltaYaw);
        entity.setPitch(entity.getPitch() + deltaPitch);
        entity.prevYaw += deltaYaw;
        entity.prevPitch += deltaPitch;
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.bodyYaw += deltaYaw;
            livingEntity.prevBodyYaw += deltaYaw;
            livingEntity.headYaw += deltaYaw;
            livingEntity.prevHeadYaw += deltaYaw;
        }
        
        Quaternionf newViewRotation = QuaternionUtil.getViewRotation(entity.getPitch(), entity.getYaw());
        
        // gravity rotation = (view rotation^-1) * camera rotation
        Quaternionf animationStartGravityRotation = new Quaternionf().set(newViewRotation).conjugate().mul(currentAnimatedCameraRotation);
        
        this.relativeRotationCenter = relativeRotationCenter;
        inAnimation = true;
        startGravityRotation = animationStartGravityRotation;
        endGravityRotation = newEndGravityRotation;
        startTimeMs = timeMs;
        endTimeMs = timeMs + durationTimeMs;
    }
    
    private Vec3d getNewLookingDirection(
        Direction newGravity, Direction prevGravity, Entity player,
        boolean rotateView
    ) {
        Vec3d oldLookingDirection = RotationUtil.vecPlayerToWorld(
            RotationUtil.rotToVec(player.getYaw(), player.getPitch()),
            prevGravity
        );
    
        if (!rotateView) {
            return oldLookingDirection;
        }
        
        if (newGravity == prevGravity.getOpposite()) {
            return oldLookingDirection.multiply(-1);
        }
        
        Quaternionf deltaRotation = QuaternionUtil.getRotationBetween(
            Vec3d.of(prevGravity.getVector()),
            Vec3d.of(newGravity.getVector())
        );
        
        Vector3f lookingDirection = new Vector3f((float) oldLookingDirection.x, (float) oldLookingDirection.y, (float) oldLookingDirection.z);
        lookingDirection.rotate(deltaRotation);
        Vec3d newLookingDirection = new Vec3d(lookingDirection);
        return newLookingDirection;
    }
    
    /**
     * It returns the rotation that applies to world for rendering.
     * To get the rotation that applies entity, conjugate it.
     */
    public Quaternionf getCurrentGravityRotation(Direction currentGravity, long timeMs) {
        
        update(timeMs);
        
        if (!inAnimation) {
            return RotationUtil.getWorldRotationQuaternion(currentGravity);
        }
        
        double delta = (double) (timeMs - startTimeMs) / (endTimeMs - startTimeMs);
        
        return RotationUtil.interpolate(
            startGravityRotation, endGravityRotation,
            mapProgress((float) delta)
        );
    }
    
    public void update(long timeMs) {
        if (timeMs > endTimeMs) {
            inAnimation = false;
        }
    }
    
    /**
     * When doing gravity flipping, the rotation center is the player bounding box center.
     * But the player feet pos changes abruptly. So we need special calculation to eye offset.
     *
     * Note when rotateView is false, it will cause non-smooth eye offset change
     */
    public Vec3d getEyeOffset(
        Quaternionf gravityRot, Vec3d localEyeOffset, Direction newGravity
    ) {
        Quaternionf gravityRotForEntity = new Quaternionf(gravityRot).conjugate();
        
        if (!inAnimation || relativeRotationCenter.equals(Vec3d.ZERO)) {
            return QuaternionUtil.rotate(localEyeOffset, gravityRotForEntity);
        }
        
        Vec3d rotationCenterOffset = RotationUtil.vecPlayerToWorld(relativeRotationCenter, newGravity);
        
        Vec3d eyeOffsetFromRotationCenter = localEyeOffset.subtract(relativeRotationCenter);
        Vec3d rotatedEyeOffsetFromRotationCenter =
            QuaternionUtil.rotate(eyeOffsetFromRotationCenter, gravityRotForEntity);
        
        return rotationCenterOffset.add(rotatedEyeOffsetFromRotationCenter);
    }
    
    private static float mapProgress(float delta) {
        return MathHelper.clamp((delta * delta * (3 - 2 * delta)), 0, 1);
    }
    
    public boolean isInAnimation() {
        return inAnimation;
    }
}
