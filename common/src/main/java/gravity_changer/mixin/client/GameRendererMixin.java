package gravity_changer.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gravity_changer.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
        method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
            ordinal = 3,
            shift = At.Shift.AFTER
        )
    )
    private void inject_renderWorld(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        ClientHooks.inject_renderWorld((GameRenderer) (Object)this,tickDelta,limitTime,matrix,ci);
    }
}
