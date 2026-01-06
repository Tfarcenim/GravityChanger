package gravity_changer.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @ModifyArgs(
            method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"
            )
    )
    public void redirect_getPositionOffset(Args args, @Local(argsOnly = true) AbstractClientPlayerEntity instance) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(instance);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Vec3d offset = new Vec3d(args.get(0), args.get(1), args.get(2));
        offset = RotationUtil.vecPlayerToWorld(offset, gravityDirection);
        args.set(0, offset.x);
        args.set(1, offset.y);
        args.set(2, offset.z);
    }

    @Redirect(
        method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"
        )
    )
    private Vec3d modify_setupTransforms_Vec3d_0(AbstractClientPlayerEntity instance, float partialTick) {
        Vec3d viewVector = instance.getRotationVec(partialTick);
        
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(instance);
        if (gravityDirection == Direction.DOWN) {
            return viewVector;
        }
        
        return RotationUtil.vecWorldToPlayer(viewVector, gravityDirection);
    }
}
