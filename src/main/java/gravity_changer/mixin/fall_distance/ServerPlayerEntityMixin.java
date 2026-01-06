package gravity_changer.mixin.fall_distance;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//TODO: Verify
@Debug(export = true)
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    
    // make sure fall distance is correct on server side of the player
    @ModifyArgs(
        method = "handleFall",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V"
        )
    )
    private void wrapFall(Args args, double dx, double dy, double dz, boolean onGround) {
        ServerPlayerEntity this_ = (ServerPlayerEntity) (Object) this;
        Direction gravity = GravityChangerAPI.getGravityDirection(this_);

        Vec3d localVec = RotationUtil.vecWorldToPlayer(dx, dy, dz, gravity);
        args.set(0, localVec.getY());
    }
    
}
