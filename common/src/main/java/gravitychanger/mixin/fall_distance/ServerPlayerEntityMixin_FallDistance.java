package gravitychanger.mixin.fall_distance;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gravitychanger.api.GravityChangerAPI;
import gravitychanger.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin_FallDistance {
    
    // make sure fall distance is correct on server side of the player
    @WrapOperation(
        method = "doCheckFallDamage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"
        )
    )
    private void wrapCheckFallDamage(
            ServerPlayer instance, double v, boolean b, BlockState state, BlockPos pos, Operation<Void> original,
            double dx, double dy, double dz, boolean onGround
    ) {
        ServerPlayer this_ = (ServerPlayer) (Object) this;
        Direction gravity = GravityChangerAPI.getGravityDirection(this_);

        Vec3 localVec = RotationUtil.vecWorldToPlayer(dx, dy, dz, gravity);
        v = localVec.y();
        original.call(instance,v,b,state,pos);
    }
    
}
