package gravity_changer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//TODO: Verify mixin works as intended
// also, first part appears to only affect crossbow firework rockets,
// so make sure arrows are handled correctly elsewhere
@Debug(export = true)
@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @Shadow private static Vector3f calcVelocity(LivingEntity shooter, Vec3d direction, float yaw) {
        //This should be replaced and never happen, if it does there is a problem
        throw new AssertionError();
    }

    @Redirect(
        method = "createArrowEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getX()D",
            ordinal = 0
        )
    )
    private double redirect_shoot_getX_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }
        
        return livingEntity.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15F, 0.0D, gravityDirection)).x;
    }
    
    @Redirect(
        method = "createArrowEntity",
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getEyeY()D",
            ordinal = 0
        )
    )
    private double redirect_shoot_getEyeY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getEyeY();
        }
        
        return livingEntity.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15F, 0.0D, gravityDirection)).y + 0.15F;
    }
    
    @Redirect(
        method = "createArrowEntity",
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_shoot_getZ_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getZ();
        }
        
        return livingEntity.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15F, 0.0D, gravityDirection)).z;
    }

    //For entities that use crossbows as weapons to accurately aim for targets
    // with different gravity directions
    @Redirect(method = "shoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/CrossbowItem;calcVelocity(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/Vec3d;F)Lorg/joml/Vector3f;"
            )
    )
    private Vector3f injected(LivingEntity shooter, Vec3d direction, float yaw, @Local ProjectileEntity projectile, @Local(ordinal = 1) LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if(gravityDirection == Direction.DOWN)
            return calcVelocity(shooter, direction, yaw);
        else {
            Vec3d targetPos = target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection));

            double d = targetPos.x - shooter.getX();
            double e = targetPos.z - shooter.getZ();

            //twice to aim for the middle of the body, once to aim above,
            // should aim above for UP and DOWN, and middle for NESW
            // maybe should be changed to aim below for UP, (player eye level)
            // but without this for UP they will never hit you (aim below target's feet, aka aims too high)
            // TODO: Figure out how to alter aim for UP direction
            double f = Math.sqrt(d * d + e * e);
            if(gravityDirection != Direction.UP)
                f = Math.sqrt(f);

            double g = targetPos.y - projectile.getY() + f * 0.2F;
            return calcVelocity(shooter, new Vec3d(d, g, e), yaw);
        }
    }
}
