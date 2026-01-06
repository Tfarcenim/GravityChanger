package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Debug(export = true)
@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {
    //IGNORED
    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    //TODO: Verify
    @ModifyVariable(
        method = "tick()V",
        at = @At(value = "STORE"), ordinal = 0
    )
    public Vec3d tick(Vec3d modify) {
        modify = new Vec3d(modify.x, modify.y + this.getFinalGravity(), modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, GravityChangerAPI.getGravityDirection(this));
        modify = new Vec3d(modify.x, modify.y - this.getFinalGravity(), modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, GravityChangerAPI.getGravityDirection(this));
        return modify;
    }
    
    //TODO: Verify
    @ModifyArgs(
        method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private static void modifyargs_init_init_0(
        Args args, EntityType<? extends ThrownEntity> type,
        LivingEntity owner, World world, ItemStack itemStack, @Nullable ItemStack shotFrom
    ) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3d pos = owner.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.1F, 0.0D, gravityDirection));
        args.set(1, pos.x);
        args.set(2, pos.y);
        args.set(3, pos.z);
    }
}
