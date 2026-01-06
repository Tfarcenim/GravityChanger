package gravity_changer.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gravity_changer.api.GravityChangerAPI;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LookTargetUtil.class)
public class LookTargetUtilMixin {
    @WrapOperation(
        method = "give(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;F)V",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"
        )
    )
    private static ItemEntity onInitItemEntity(
        World level, double posX, double posY, double posZ, ItemStack itemStack,
        Operation<ItemEntity> operation,
        @Local float yOffset, @Local LivingEntity entity
    ) {
        Vec3d eyeOffset = GravityChangerAPI.getEyeOffset(entity);
        Vec3d offset = eyeOffset.normalize().multiply(yOffset);
        Vec3d itemPos = entity.getPos().add(eyeOffset).subtract(offset);
        ItemEntity itemEntity = operation.call(
            level, itemPos.getX(), itemPos.getY(), itemPos.getZ(), itemStack
        );
        GravityChangerAPI.setBaseGravityDirection(
            itemEntity,
            GravityChangerAPI.getGravityDirection(entity)
        );
        return itemEntity;
    }
    
    @WrapOperation(
        method = "give(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;F)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ItemEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
        )
    )
    private static void onSetVelocity(
        ItemEntity itemEntity, Vec3d deltaMovement,
        Operation<Void> operation,
        @Local(argsOnly = true) LivingEntity entity
    ) {
        GravityChangerAPI.setWorldVelocity(entity, deltaMovement);
    }
    
}
