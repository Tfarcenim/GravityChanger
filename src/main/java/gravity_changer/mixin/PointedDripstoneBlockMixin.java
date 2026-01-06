package gravity_changer.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gravity_changer.api.GravityChangerAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    // use Comparable<Direction> instead of Direction because of erased signature
    @WrapOperation(
        method = "onLandedUpon",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;",
            ordinal = 0
        )
    )
    private Comparable<Direction> wrapOperation_onLandedUpon_get_0(BlockState blockState, Property<Direction> property, Operation<Comparable<Direction>> original, World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(blockState, property);
        }
        
        return original.call(blockState, property) == gravityDirection.getOpposite() ? Direction.UP : Direction.DOWN;
    }
}
