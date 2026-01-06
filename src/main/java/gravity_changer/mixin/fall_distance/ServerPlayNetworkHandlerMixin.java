package gravity_changer.mixin.fall_distance;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//TODO: Verify this does what I want it to, also verify literally every single other mixin later
@Debug(export = true)
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

	@Shadow
	public ServerPlayerEntity player;

	@Shadow
	private double updatedX, updatedY, updatedZ;

	@ModifyVariable(
			method = "onPlayerMove",
			at = @At(value = "STORE"),
			ordinal = 1
	)
	private boolean modifyFlagBasedOnGravity(boolean originalFlag, PlayerMoveC2SPacket packet) {
		Direction gravity = GravityChangerAPI.getGravityDirection(player);

		double dx = packet.getX(player.getX()) - updatedX;
		double dy = packet.getY(player.getY()) - updatedY;
		double dz = packet.getZ(player.getZ()) - updatedZ;

		Vec3d localVec = RotationUtil.vecWorldToPlayer(dx, dy, dz, gravity);
		return localVec.y > 0.0;
	}
}
