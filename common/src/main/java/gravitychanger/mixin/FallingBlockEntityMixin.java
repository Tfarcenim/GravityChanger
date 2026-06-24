package gravitychanger.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    
    //@ModifyReturnValue(method = "fall", at = @At("RETURN"))
    //private static FallingBlockEntity applyGravityF(FallingBlockEntity entity, @Local BlockPos pos) {
    //    final Direction gravity = GeneralUtil.getGravityForBlockPos((ServerWorld)entity.world, pos);
    //    GravityChangerAPIFabric.addGravity(entity, new Gravity(gravity, 5, 2, "star_heart"));
    //    if (gravity != Direction.DOWN) {
    //        entity.velocityDirty = true;
    //    }
    //    return entity;
    //}
//
    //@Inject(method = "tick", at = @At("HEAD"))
    //private void applyGravityT(CallbackInfo ci) {
    //    GeneralUtil.setAppropriateEntityGravity(this);
    //    if (GravityChangerAPIFabric.getGravityDirection(this) != Direction.DOWN) {
    //        velocityDirty = true;
    //    }
    //}
//
    //@Redirect(
    //    method = "tick",
    //    at = @At(
    //        value = "INVOKE",
    //        target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
    //    )
    //)
    //private BlockPos relativeToGravity(BlockPos instance) {
    //    return instance.offset(GeneralUtil.getGravityForBlockPos((ServerWorld)world, instance));
    //}
//
    //@Override
    //protected Box calculateBoundingBox() {
    //    final Box original = super.calculateBoundingBox();
    //    final Direction gravity = GravityChangerAPIFabric.getGravityDirection(this);
    //    if (gravity == Direction.DOWN) {
    //        return original;
    //    }
    //    return original.offset(gravity.getOffsetX() * 0.5, 0.5, gravity.getOffsetZ() * 0.5);
    //}
}
