package gravity_changer;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClientHooksForge {

    //todo, adapt properly to forge hook
    public static void inject_getInWallBlockState(Player player, CallbackInfoReturnable<Pair<BlockState, BlockPos>> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(player);
        if (gravityDirection == Direction.DOWN) return;

        cir.cancel();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        Vec3 eyePos = player.getEyePosition();
        Vector3f multipliers = RotationUtil.vecPlayerToWorld(player.getBbWidth() * 0.8F, 0.1F, player.getBbWidth() * 0.8F, gravityDirection);
        for (int i = 0; i < 8; ++i) {
            double d = eyePos.x + (((i >> 0) % 2) - 0.5F) * multipliers.x();
            double e = eyePos.y + (((i >> 1) % 2) - 0.5F) * multipliers.y();
            double f = eyePos.z + (((i >> 2) % 2) - 0.5F) * multipliers.z();
            mutable.set(d, e, f);
            BlockState blockState = player.level().getBlockState(mutable);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(player.level(), mutable)) {
                cir.setReturnValue(Pair.of(blockState,mutable.immutable()));
            }
        }
        cir.setReturnValue(null);
    }

}
