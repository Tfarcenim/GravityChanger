package gravity_changer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);
    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);
    @Shadow public abstract float getYaw(float tickDelta);
    
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    
    @Redirect(
        method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getY()D",
            ordinal = 0
        )
    )
    private double redirect_travel_getY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }
        
        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }
    
    @Redirect(
        method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getY()D",
            ordinal = 1
        )
    )
    private double redirect_travel_getY_1(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }
        
        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }
    
    @Redirect(
        method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getY()D",
            ordinal = 2
        )
    )
    private double redirect_travel_getY_2(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }
        
        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }
    
    @Redirect(
        method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getY()D",
            ordinal = 3
        )
    )
    private double redirect_travel_getY_3(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }
        
        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }
    
    @ModifyVariable(
        method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        ),
        ordinal = 2
    )
    private Vec3d modify_travel_Vec3d_2(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    @ModifyArg(
        method = "playBlockFallSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
        ),
        index = 0
    )
    private BlockPos modify_playBlockFallSound_getBlockState_0(BlockPos blockPos) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return blockPos;
        }
        
        return BlockPos.ofFloored(this.getPos().add(RotationUtil.vecPlayerToWorld(0, -0.20000000298023224D, 0, gravityDirection)));
    }
    
    @Redirect(
        method = "canSee(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        )
    )
    private Vec3d redirect_canSee_new_0(double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }
        
        return this.getEyePos();
    }
    
    @Redirect(
        method = "canSee(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "NEW",
            target = "(DDD)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 1
        )
    )
    private Vec3d redirect_canSee_new_1(double x, double y, double z, Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }
        
        return entity.getEyePos();
    }
    
    @Inject(
        method = "getBoundingBox(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/util/math/Box;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inject_getBoundingBox(EntityPose pose, CallbackInfoReturnable<Box> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Box box = cir.getReturnValue();
        if (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection));
    }
    
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getX()D",
            ordinal = 0
        )
    )
    private double wrapOperation_tick_getX_0(LivingEntity livingEntity, Operation<Double> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(livingEntity);
        }
        
        return RotationUtil.vecWorldToPlayer(original.call(livingEntity) - livingEntity.prevX, livingEntity.getY() - livingEntity.prevY, livingEntity.getZ() - livingEntity.prevZ, gravityDirection).x + livingEntity.prevX;
    }
    
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
            ordinal = 0
        )
    )
    private double wrapOperation_tick_getZ_0(LivingEntity livingEntity, Operation<Double> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(livingEntity);
        }
        
        return RotationUtil.vecWorldToPlayer(livingEntity.getX() - livingEntity.prevX, livingEntity.getY() - livingEntity.prevY, original.call(livingEntity) - livingEntity.prevZ, gravityDirection).z + livingEntity.prevZ;
    }

    //TODO: Verify this and the next, there was a significant change
    @Redirect(
        method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;getX()D",
            ordinal = 0
        )
    )
    private double redirect_damage_getX_0(Vec3d damageSourcePosition, @Local(ordinal = 0) Entity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return damageSourcePosition.getX();
            }
            else {
                return attacker.getEyePos().x;
            }
        }
        
        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).x;
    }
    
    @Redirect(
        method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_damage_getZ_0(Vec3d damageSourcePosition, @Local(ordinal = 0) Entity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return damageSourcePosition.getZ();
            }
            else {
                return attacker.getEyePos().z;
            }
        }
        
        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).z;
    }
    
    @Redirect(
        method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getX()D",
            ordinal = 0
        )
    )
    private double redirect_damage_getX_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getX();
        }
        
        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).x;
    }
    
    @Redirect(
        method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_damage_getZ_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getZ();
        }
        
        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).z;
    }
    
    @Redirect(
        method = "knockback(Lnet/minecraft/entity/LivingEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getX()D",
            ordinal = 0
        )
    )
    private double redirect_knockback_getX_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getX();
        }
        
        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).x;
    }
    
    
    @Redirect(
        method = "knockback(Lnet/minecraft/entity/LivingEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_knockback_getZ_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getZ();
        }
        
        return RotationUtil.vecWorldToPlayer(target.getPos(), gravityDirection).z;
    }
    
    @Redirect(
        method = "knockback(Lnet/minecraft/entity/LivingEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getX()D",
            ordinal = 1
        )
    )
    private double redirect_knockback_getX_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getX();
            }
            else {
                return attacker.getEyePos().x;
            }
        }
        
        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).x;
    }
    
    @Redirect(
        method = "knockback(Lnet/minecraft/entity/LivingEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getZ()D",
            ordinal = 1
        )
    )
    private double redirect_knockback_getZ_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getZ();
            }
            else {
                return attacker.getEyePos().z;
            }
        }
        
        return RotationUtil.vecWorldToPlayer(attacker.getEyePos(), gravityDirection).z;
    }
    
    @Redirect(
        method = "baseTick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/BlockPos;ofFloored(DDD)Lnet/minecraft/util/math/BlockPos;",
            ordinal = 0
        )
    )
    private BlockPos redirect_baseTick_new_0(double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return BlockPos.ofFloored(x, y, z);
        }
        
        return BlockPos.ofFloored(this.getEyePos());
    }
    
    @WrapOperation(
        method = "spawnItemParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        )
    )
    private Vec3d wrapOperation_spawnItemParticles_add_0(Vec3d vec3d, double x, double y, double z, Operation<Vec3d> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return original.call(vec3d, x, y, z);
        }
        
        Vec3d rotated = RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
        return original.call(this.getEyePos(), rotated.x, rotated.y, rotated.z);
    }
    
    @ModifyVariable(
        method = "spawnItemParticles(Lnet/minecraft/item/ItemStack;I)V",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/util/math/Vec3d;rotateY(F)Lnet/minecraft/util/math/Vec3d;",
            ordinal = 0
        ),
        ordinal = 0
    )
    private Vec3d modify_spawnItemParticles_Vec3d_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    //Todo: Verify this causes no problems, was some changes here
    @ModifyArgs(
        method = "tickStatusEffects",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
        )
    )
    private void modify_tickStatusEffects_addParticle_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(args.get(1), args.get(2), args.get(3)), gravityDirection));
        args.set(1, vec3d.x);
        args.set(2, vec3d.y);
        args.set(3, vec3d.z);
    }

    //Todo: Verify this causes no problems, was some changes here
    @ModifyArgs(
        method = "addDeathParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
            ordinal = 0
        )
    )
    private void modify_addDeathParticless_addParticle_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(args.get(1), args.get(2), args.get(3)), gravityDirection));
        args.set(1, vec3d.x);
        args.set(2, vec3d.y);
        args.set(3, vec3d.z);
    }

    @ModifyVariable(
            method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private double injected(double d) {
        return d * GravityChangerAPI.getGravityStrength(this);
    }
    
    @ModifyVariable(method = "computeFallDamage(FF)I", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float diminishFallDamage(float value) {
        return value * (float) Math.sqrt(GravityChangerAPI.getGravityStrength(this));
    }

    @Shadow public abstract void updateLimbs(boolean flutter);

    @Shadow protected abstract void updateLimbs(float limbDistance);

    //TODO: Was removed, might not work
    @Inject(
            method = "updateLimbs(Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_updateLimbs(boolean flutter, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if(gravityDirection == Direction.DOWN) return;

        ci.cancel();

        Vec3d playerPosDelta = RotationUtil.vecWorldToPlayer(this.getX() - this.prevX, this.getY() - this.prevY, this.getZ() - this.prevZ, gravityDirection);

        float mag = (float) MathHelper.magnitude(playerPosDelta.x,flutter ? playerPosDelta.y : 0.0D,playerPosDelta.z);
        this.updateLimbs(mag);
    }


    // TODO shield knockback
    @ModifyVariable(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/LivingEntity;getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3d modify_blockedByShield_Vec3d_1(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity)(Object)this);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //TODO: Was commented out, might not work
    @ModifyArg(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;relativize(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            index = 0
    )
    private Vec3d modify_blockedByShield_relativize_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity)(Object)this);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return this.getEyePos();
    }

    //TODO: Was commented out, might not work
    @ModifyVariable(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/Vec3d;normalize()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            ),
            ordinal = 2
    )
    private Vec3d modify_blockedByShield_Vec3d_2(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity)(Object)this);
        if(gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
