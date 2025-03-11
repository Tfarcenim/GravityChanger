package gravity_changer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClientHooks {

    public static void inject_getInWallBlockState(Player player, CallbackInfoReturnable<BlockState> cir) {
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
                cir.setReturnValue(blockState);
            }
        }

        cir.setReturnValue(null);
    }

    public static void inject_setRotation(Camera camera, CallbackInfo ci) {
        Entity entity = camera.getEntity();
        if (entity != null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            RotationAnimation animation = GravityChangerAPI.getRotationAnimation(entity);
            if (animation == null) {
                return;
            }
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
                return;
            }
            float partialTick = Minecraft.getInstance().getFrameTime();
            long timeMs = entity.level().getGameTime() * 50 + (long) (partialTick * 50);
            Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
            rotation.conjugate();
            rotation.mul(camera.rotation());
            camera.rotation().set(rotation.x(), rotation.y(), rotation.z(), rotation.w());
        }
    }

    public static void wrapOperation_update_setPos_0(
            Camera camera, double x, double y, double z,
            Operation<Void> original, BlockGetter area, Entity focusedEntity,
            boolean thirdPerson, boolean inverseView, float tickDelta,float eyeHeightOld, float eyeHeight
    ) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        RotationAnimation animation = GravityChangerAPI.getRotationAnimation(focusedEntity);

        if (animation == null) {
            original.call(camera, x, y, z);
            return;
        }

        float partialTick = Minecraft.getInstance().getFrameTime();
        long timeMs = focusedEntity.level().getGameTime() * 50 + (long) (partialTick * 50);
        animation.update(timeMs);
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            original.call(camera, x, y, z);
            return;
        }

        Quaternionf gravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);

        double entityX = Mth.lerp(tickDelta, focusedEntity.xo, focusedEntity.getX());
        double entityY = Mth.lerp(tickDelta, focusedEntity.yo, focusedEntity.getY());
        double entityZ = Mth.lerp(tickDelta, focusedEntity.zo, focusedEntity.getZ());

        double currentCameraY = Mth.lerp(tickDelta, eyeHeightOld, eyeHeight);

        Vec3 eyeOffset = animation.getEyeOffset(
                gravityRotation,
                new Vec3(0, currentCameraY, 0),
                gravityDirection
        );

        original.call(
                camera,
                entityX + eyeOffset.x(),
                entityY + eyeOffset.y(),
                entityZ + eyeOffset.z()
        );
    }
}
