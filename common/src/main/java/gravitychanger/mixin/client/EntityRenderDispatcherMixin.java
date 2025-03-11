package gravitychanger.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gravitychanger.GravityChangerClient;
import gravitychanger.RotationAnimation;
import gravitychanger.api.GravityChangerAPI;
import gravitychanger.util.RotationUtil;
import gravitychanger.EntityTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    @Final
    private static RenderType SHADOW_RENDER_TYPE;
    
    @Shadow
    private boolean shouldRenderShadow;

    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    private void inject_render_0(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof Projectile) && !(entity instanceof ExperienceOrb) && EntityTags.allowGravityTransformationInRendering(entity)) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            if (!this.shouldRenderShadow) return;
            
            matrices.pushPose();
            RotationAnimation animation = GravityChangerAPI.getRotationAnimation(entity);
            if (animation == null) {
                return;
            }
            long timeMs = entity.level().getGameTime() * 50 + (long) (tickDelta * 50);
            matrices.mulPose(new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs)).conjugate());
        }
    }
    
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 1
        )
    )
    private void inject_render_1(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof Projectile) && !(entity instanceof ExperienceOrb) && EntityTags.allowGravityTransformationInRendering(entity)) {
            if (!this.shouldRenderShadow) return;
            matrices.popPose();
        }
    }
    
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    private void inject_render_2(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof Projectile) && !(entity instanceof ExperienceOrb) && EntityTags.allowGravityTransformationInRendering(entity)) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            if (gravityDirection == Direction.DOWN) return;
            if (!this.shouldRenderShadow) return;
            
            matrices.mulPose(RotationUtil.getCameraRotationQuaternion(gravityDirection));
        }
    }
    
    @Inject(
        method = "renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void inject_renderShadow(PoseStack matrices, MultiBufferSource vertexConsumers, Entity entity, float opacity, float tickDelta, LevelReader world, float radius, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) return;
        
        ci.cancel();
        
        double x = Mth.lerp(tickDelta, entity.xOld, entity.getX());
        double y = Mth.lerp(tickDelta, entity.yOld, entity.getY());
        double z = Mth.lerp(tickDelta, entity.zOld, entity.getZ());
        Vec3 minShadowPos = RotationUtil.vecPlayerToWorld(-radius, -radius, (double) -radius, gravityDirection).add(x, y, z);
        Vec3 maxShadowPos = RotationUtil.vecPlayerToWorld(radius, 0.0D, radius, gravityDirection).add(x, y, z);
        PoseStack.Pose entry = matrices.last();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_RENDER_TYPE);
        
        for (BlockPos blockPos : BlockPos.betweenClosed(BlockPos.containing(minShadowPos), BlockPos.containing(maxShadowPos))) {
            GravityChangerClient.gravitychanger$renderShadowPartPlayer(entry, vertexConsumer, world, blockPos, x, y, z, radius, opacity, gravityDirection);
        }
    }

    @ModifyVariable(
        method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;",
            ordinal = 0
        ),
        ordinal = 0
    )
    private static AABB modify_renderHitbox_Box_0(AABB box, PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return box;
        }
        
        return RotationUtil.boxWorldToPlayer(box, gravityDirection);
    }
    
    @Redirect(
        method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0
        )
    )
    private static Vec3 redirectViewVector(Entity instance, float partialTicks) {
        Vec3 viewVector = instance.getViewVector(partialTicks);
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(instance);
        if (gravityDirection == Direction.DOWN) {
            return viewVector;
        }
        
        return RotationUtil.vecWorldToPlayer(viewVector, gravityDirection);
    }
}
