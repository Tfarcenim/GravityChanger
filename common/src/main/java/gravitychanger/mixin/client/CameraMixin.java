package gravitychanger.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gravitychanger.RotationAnimation;
import gravitychanger.api.GravityChangerAPI;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

//TODO: Appears unchanged, but verify
@Mixin(value = Camera.class, priority = 1001)
public abstract class CameraMixin {

    @Shadow private Entity entity;

    @Shadow @Final private Quaternionf rotation;

    @Shadow private float eyeHeightOld;

    @Shadow private float eyeHeight;

    @WrapOperation(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
                    ordinal = 0
            )
    )
    private void wrapOperation_update_setPos_0(
            Camera camera, double x, double y, double z,
            Operation<Void> original, BlockGetter area, Entity focusedEntity,
            boolean thirdPerson, boolean inverseView, float tickDelta
    ) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        RotationAnimation animation = GravityChangerAPI.getRotationAnimation(focusedEntity);

        if (animation == null) {
            original.call(this, x, y, z);
            return;
        }

        //TODO: Check if we want this to return 1.0 getTickDelta(false) or the real value
        // while tick freeze is active
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);//getTickDelta();
        long timeMs = focusedEntity.level().getGameTime() * 50 + (long) (partialTick * 50);
        animation.update(timeMs);
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            original.call(this, x, y, z);
            return;
        }

        Quaternionf gravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);

        double entityX = Mth.lerp((double) tickDelta, focusedEntity.xo, focusedEntity.getX());
        double entityY = Mth.lerp((double) tickDelta, focusedEntity.yo, focusedEntity.getY());
        double entityZ = Mth.lerp((double) tickDelta, focusedEntity.zo, focusedEntity.getZ());

        double currentCameraY = Mth.lerp(tickDelta, this.eyeHeightOld, this.eyeHeight);

        Vec3 eyeOffset = animation.getEyeOffset(
                gravityRotation,
                new Vec3(0, currentCameraY, 0),
                gravityDirection
        );

        original.call(
                this,
                entityX + eyeOffset.x(),
                entityY + eyeOffset.y(),
                entityZ + eyeOffset.z()
        );
    }

    /*@Inject(neoforge patches this
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if (this.entity != null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.entity);
            RotationAnimation animation = GravityChangerAPI.getRotationAnimation(entity);
            if (animation == null) {
                return;
            }
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
                return;
            }

            //TODO: Check if we want this to return 1.0 getTickDelta(false) or the real value
            // while tick freeze is active
            float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);//getTickDelta();
            long timeMs = entity.level().getGameTime() * 50 + (long) (partialTick * 50);
            Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
            rotation.conjugate();
            rotation.mul(this.rotation);
            this.rotation.set(rotation.x(), rotation.y(), rotation.z(), rotation.w());
        }
    }*/

    //Old method for reference
    /*@Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
                    shift = At.Shift.AFTER
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if(this.focusedEntity !=null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.focusedEntity);
            Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
            if(animationOptional.isEmpty()) return;
            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) return;
            long timeMs = focusedEntity.getWorld().getTime()*50+(long)(storedTickDelta*50);
            Quaternionf rotation = animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();
            Quaternionf product = CompatMath.hamiltonProduct(rotation,this.rotation);
            this.rotation.set(product.x(), product.y(), product.z(), product.w());
        }
    }*/
}
