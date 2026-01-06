package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    
    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(
        method = "onPlayerMove",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
        )
    )
    private Vec3d modify_onPlayerMove_move_1(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    @ModifyArg(
        method = "onVehicleMove",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
        ),
        index = 1
    )
    private Vec3d modify_onVehicleMove_move_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    @ModifyArgs(
        method = "isEntityOnAir",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
        )
    )
    private void modify_onVehicleMove_move_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        Vec3d argVec = new Vec3d(args.get(0), args.get(1), args.get(2));
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        
        args.set(0, argVec.x);
        args.set(1, argVec.y);
        args.set(2, argVec.z);
        
    }
}
