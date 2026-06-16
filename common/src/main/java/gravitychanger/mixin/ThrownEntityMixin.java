package gravitychanger.mixin;


import gravitychanger.api.GravityChangerAPI;
import gravitychanger.util.RotationUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrowableProjectile.class)
public abstract class ThrownEntityMixin {
    
    @Shadow
    protected abstract double getDefaultGravity();

    /*@Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        return GravityChangerAPIFabric.getGravityDirection((ThrownEntity)(Object)this);
    }*/
    
    @ModifyVariable(
        method = "tick()V",
        at = @At(
            value = "STORE"
        )
        , ordinal = 0
    )
    public Vec3 tick(Vec3 modify) {
        //if(this instanceof RotatableEntityAccessor) {
        modify = new Vec3(modify.x, modify.y + this.getDefaultGravity(), modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, GravityChangerAPI.getGravityDirection((ThrowableProjectile) (Object) this));
        modify = new Vec3(modify.x, modify.y - this.getDefaultGravity(), modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, GravityChangerAPI.getGravityDirection((ThrowableProjectile) (Object) this));
        // }
        return modify;
    }
    
    /*@ModifyArgs(todo fix
        method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;)V",
            ordinal = 0
        )
    )
    private static void modifyargs_init_init_0(Args args, EntityType<? extends ThrowableProjectile> type, LivingEntity owner, Level world) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3 pos = owner.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.10000000149011612D, 0.0D, gravityDirection));
        args.set(1, pos.x);
        args.set(2, pos.y);
        args.set(3, pos.z);
    }*/
    
    @ModifyReturnValue(method = "getDefaultGravity", at = @At("RETURN"))
    private double multiplyGravity(double original) {
        return original * GravityChangerAPI.getGravityStrength(((Entity) (Object) this));
    }
}
