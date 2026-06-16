package gravitychanger.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gravitychanger.RotationAnimation;
import gravitychanger.api.GravityChangerAPI;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;
    
    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V",
            shift = At.Shift.AFTER
        )
    )
    private void inject_renderWorld(DeltaTracker deltaTracker, CallbackInfo ci) {
        Entity focusedEntity = this.mainCamera.getEntity();
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        RotationAnimation animation = GravityChangerAPI.getRotationAnimation(focusedEntity);
        if (animation == null) {
            return;
        }
        long timeMs = focusedEntity.level().getGameTime() * 50 + (long) (deltaTracker.getGameTimeDeltaPartialTick(true) * 50);
        Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);

        if (animation.isInAnimation()) {
            // make sure that frustum culling updates when running rotation animation
            Minecraft.getInstance().levelRenderer.needsUpdate();
        }
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(currentGravityRotation);
    }
}
