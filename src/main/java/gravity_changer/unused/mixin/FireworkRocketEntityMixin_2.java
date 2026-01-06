package gravity_changer.unused.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin_2 extends Entity {

    @Shadow
    private @Nullable LivingEntity shooter;

    public FireworkRocketEntityMixin_2(EntityType<?> type, World world) { super(type, world); }
    
    /*@Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return GravityChangerAPI.getGravityDirection(vehicle);
        }

        return GravityChangerAPI.getGravityDirection((FireworkRocketEntity)(Object)this);
    }*/
    
    // @ModifyVariable(
    //         method = "tick",
    //         at = @At(
    //                 value = "INVOKE_ASSIGN",
    //                 target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;",
    //                 ordinal = 0
    //         )
    // )
    // private Vec3d modify_tick_Vec3d_0(Vec3d vec3d) {
    //     assert this.shooter != null;
    //     Direction gravityDirection = ((EntityAccessor) this.shooter).gravitychanger$getAppliedGravityDirection();
    //     if(gravityDirection == Direction.DOWN) {
    //         return vec3d;
    //     }
//
    //     return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    // }
}
