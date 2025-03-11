package gravity_changer.mixin.client;

import gravity_changer.ClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Camera.class, priority = 1001)
public abstract class CameraMixin {

    @Shadow
    private float eyeHeightOld;
    
    @Shadow
    private float eyeHeight;
    
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
        ClientHooks.wrapOperation_update_setPos_0(camera,x,y,z,original,area,focusedEntity,thirdPerson,inverseView,tickDelta,eyeHeightOld,eyeHeight);
    }
    
    @Inject(
        method = "setRotation(FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
            shift = At.Shift.AFTER,
            remap = false
        )
    )
    private void inject_setRotation(CallbackInfo ci) {
        ClientHooks.inject_setRotation((Camera) (Object)this,ci);
    }
}
