package gravity_changer.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gravity_changer.RotationAnimation;
import gravity_changer.api.GravityChangerAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

//TODO: Appears unchanged, but verify
@Mixin(value = Camera.class, priority = 1001)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);
    
    @Shadow private Entity focusedEntity;
    
    @Shadow @Final private Quaternionf rotation;
    
    @Shadow private float lastCameraY;
    
    @Shadow private float cameraY;
    
    @WrapOperation(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
            ordinal = 0
        )
    )
    private void wrapOperation_update_setPos_0(
        Camera camera, double x, double y, double z,
        Operation<Void> original, BlockView area, Entity focusedEntity,
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
        float partialTick = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);//getTickDelta();
        long timeMs = focusedEntity.getWorld().getTime() * 50 + (long) (partialTick * 50);
        animation.update(timeMs);
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            original.call(this, x, y, z);
            return;
        }
    
        Quaternionf gravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);
        
        double entityX = MathHelper.lerp((double) tickDelta, focusedEntity.prevX, focusedEntity.getX());
        double entityY = MathHelper.lerp((double) tickDelta, focusedEntity.prevY, focusedEntity.getY());
        double entityZ = MathHelper.lerp((double) tickDelta, focusedEntity.prevZ, focusedEntity.getZ());
        
        double currentCameraY = MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY);
    
        Vec3d eyeOffset = animation.getEyeOffset(
            gravityRotation,
            new Vec3d(0, currentCameraY, 0),
            gravityDirection
        );
        
        original.call(
            this,
            entityX + eyeOffset.getX(),
            entityY + eyeOffset.getY(),
            entityZ + eyeOffset.getZ()
        );
    }
    
    @Inject(
        method = "Lnet/minecraft/client/render/Camera;setRotation(FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
            shift = At.Shift.AFTER,
            remap = false
        )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if (this.focusedEntity != null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.focusedEntity);
            RotationAnimation animation = GravityChangerAPI.getRotationAnimation(focusedEntity);
            if (animation == null) {
                return;
            }
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
                return;
            }

            //TODO: Check if we want this to return 1.0 getTickDelta(false) or the real value
            // while tick freeze is active
            float partialTick = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);//getTickDelta();
            long timeMs = focusedEntity.getWorld().getTime() * 50 + (long) (partialTick * 50);
            Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
            rotation.conjugate();
            rotation.mul(this.rotation);
            this.rotation.set(rotation.x(), rotation.y(), rotation.z(), rotation.w());
        }
    }

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
