package gravity_changer.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import gravity_changer.RotationAnimation;
import gravity_changer.api.GravityChangerAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Verified Working as of 1.20.6
@Debug(export = true)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera camera;

    //TODO: Figure out a better way to do this than shift.
    //TODO: For some reason commenting everything out fixed it??? What changed to make this unneeded?

    //TODO: Not sure what is/isn't working elsewhere that made this pair with the CameraMixin
    // act differently, but I make sure to do the terrain update here now so we don't have culling issues

    //TODO: unable to locate method mapping
    @Inject(
        method = "Lnet/minecraft/client/render/GameRenderer;renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Matrix4f;rotation(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix4f;",
            shift = At.Shift.BY, by = 2, remap = false
        )
    )
    private void inject_renderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2) {
        //OLD
        /*if (this.camera.getFocusedEntity() != null) {
            Entity focusedEntity = this.camera.getFocusedEntity();
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
            RotationAnimation animationOptional = GravityChangerAPI.getRotationAnimation(focusedEntity);
            long timeMs = focusedEntity.getWorld().getTime()*50+(long)(tickDelta*50);
            Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);
            matrix.multiply(currentGravityRotation);
        }*/

        if (this.camera.getFocusedEntity() != null) {
            Entity focusedEntity = this.camera.getFocusedEntity();
            //Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);

            RotationAnimation animation = GravityChangerAPI.getRotationAnimation(focusedEntity);
            if (animation == null) {
                return;
            }

            //tod: Check if we want this to return 1.0 getTickDelta(false) or the real value
            // while tick freeze is active
            //long timeMs = focusedEntity.getWorld().getTime() * 50 + (long) (tickCounter.getTickDelta(true) * 50);
            //Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);

            if (animation.isInAnimation()) {
                // make sure that frustum culling updates when running rotation animation
                MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
            }
            //matrix4f2.rotate(currentGravityRotation);
        }
    }
}
