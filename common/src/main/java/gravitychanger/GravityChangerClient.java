package gravitychanger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gravitychanger.api.GravityChangerAPI;
import gravitychanger.util.RotationUtil;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class GravityChangerClient {
    public static void gravitychanger$renderShadowPartPlayer(PoseStack.Pose entry, VertexConsumer vertices, LevelReader world, BlockPos pos, double x, double y, double z, float radius, float opacity, Direction gravityDirection) {
        BlockPos posBelow = pos.relative(gravityDirection);
        BlockState blockStateBelow = world.getBlockState(posBelow);
        if (blockStateBelow.getRenderShape() != RenderShape.INVISIBLE && world.getMaxLocalRawBrightness(pos) > 3) {
            if (blockStateBelow.isCollisionShapeFullBlock(world, posBelow)) {
                VoxelShape voxelShape = blockStateBelow.getShape(world, posBelow);
                if (!voxelShape.isEmpty()) {
                    Vec3 playerPos = RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection);
                    float alpha = (float) (((double) opacity - (playerPos.y - (RotationUtil.vecWorldToPlayer(Vec3.atCenterOf(pos), gravityDirection).y - 0.5D)) / 2.0D) * 0.5D * (double) world.getLightLevelDependentMagicValue(pos));
                    if (alpha >= 0.0F) {
                        if (alpha > 1.0F) {
                            alpha = 1.0F;
                        }
                        int k = FastColor.ARGB32.color(Mth.floor(alpha * 255.0F), 255, 255, 255);
                        Vec3 centerPos = Vec3.atCenterOf(pos);
                        Vec3 playerCenterPos = RotationUtil.vecWorldToPlayer(centerPos, gravityDirection);

                        Vec3 playerRelNN = playerCenterPos.add(-0.5D, -0.5D, -0.5D).subtract(playerPos);
                        Vec3 playerRelPP = playerCenterPos.add(0.5D, -0.5D, 0.5D).subtract(playerPos);

                        Vec3 relNN = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(-0.5D, -0.5D, -0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3 relNP = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(-0.5D, -0.5D, 0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3 relPN = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(0.5D, -0.5D, -0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);
                        Vec3 relPP = RotationUtil.vecWorldToPlayer(centerPos.add(RotationUtil.vecPlayerToWorld(0.5D, -0.5D, 0.5D, gravityDirection)).subtract(x, y, z), gravityDirection);

                        float minU = -(float) playerRelNN.x / 2.0F / radius + 0.5F;
                        float maxU = -(float) playerRelPP.x / 2.0F / radius + 0.5F;
                        float minV = -(float) playerRelNN.z / 2.0F / radius + 0.5F;
                        float maxV = -(float) playerRelPP.z / 2.0F / radius + 0.5F;

                        EntityRenderDispatcher.shadowVertex(entry, vertices, k, (float) relNN.x, (float) relNN.y, (float) relNN.z, minU, minV);
                        EntityRenderDispatcher.shadowVertex(entry, vertices, k, (float) relNP.x, (float) relNP.y, (float) relNP.z, minU, maxV);
                        EntityRenderDispatcher.shadowVertex(entry, vertices, k, (float) relPP.x, (float) relPP.y, (float) relPP.z, maxU, maxV);
                        EntityRenderDispatcher.shadowVertex(entry, vertices, k, (float) relPN.x, (float) relPN.y, (float) relPN.z, maxU, minV);
                    }
                }
            }
        }
    }

    public static void inject_getInWallBlockState(Player player, CallbackInfoReturnable<BlockState> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(player);
        if (gravityDirection == Direction.DOWN) return;

        cir.cancel();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        Vec3 eyePos = player.getEyePosition();
        Vector3f multipliers = RotationUtil.vecPlayerToWorld(player.getBbWidth() * 0.8F, 0.1F, player.getBbWidth() * 0.8F, gravityDirection);
        for (int i = 0; i < 8; ++i) {
            double d = eyePos.x + (double) (((float) ((i) % 2) - 0.5F) * multipliers.x());
            double e = eyePos.y + (double) (((float) ((i >> 1) % 2) - 0.5F) * multipliers.y());
            double f = eyePos.z + (double) (((float) ((i >> 2) % 2) - 0.5F) * multipliers.z());
            mutable.set(d, e, f);
            BlockState blockState = player.level().getBlockState(mutable);
            if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(player.level(), mutable)) {
                cir.setReturnValue(blockState);
            }
        }

        cir.setReturnValue(null);
    }
}
