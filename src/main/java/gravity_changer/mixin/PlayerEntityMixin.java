package gravity_changer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gravity_changer.GravityChangerMod;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//TODO: Double check the return ordinal stuff, idk why it did that
@Debug(export = true)
@Mixin(value = PlayerEntity.class, priority = 1001)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @WrapOperation(
        method = "travel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
        )
    )
    private Vec3d wrapOperation_travel_getRotationVector_0(PlayerEntity playerEntity, Operation<Vec3d> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(playerEntity);
        }
        
        return RotationUtil.vecWorldToPlayer(original.call(playerEntity), gravityDirection);
    }
    
    
    @ModifyArgs(
        method = "travel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/BlockPos;ofFloored(DDD)Lnet/minecraft/util/math/BlockPos;"
        )
    )
    private void modify_move_multiply_0(Args args) {
        Vec3d rotate = new Vec3d(0.0D, 1.0D - 0.1D, 0.0D);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection(this));
        args.set(0, (double) args.get(0) - rotate.x);
        args.set(1, (double) args.get(1) - rotate.y + (1.0D - 0.1D));
        args.set(2, (double) args.get(2) - rotate.z);
    }
    
    @Redirect(
        method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;",
            ordinal = 0
        )
    )
    private ItemEntity redirect_dropItem_new_0(
        World world, double x, double y, double z, ItemStack stack
    ) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return new ItemEntity(world, x, y, z, stack);
        }
        
        Vec3d vec3d = this.getEyePos()
            .subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.3D, 0.0D, gravityDirection));

        ItemEntity itemEntity = new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, stack);

//        // change the gravity of the thrown item
//        GravityChangerAPI.setBaseGravityDirection(
//            itemEntity, gravityDirection
//        );
        // the item entity calculates getPos both on client and server separately
        // if gravity is not down, the client and server will desync (the reason is not yet known)
        // don't let item change gravity for now
        
        return itemEntity;
    }
    
    @WrapOperation(
        method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"
        )
    )
    private void wrapOperation_dropItem_setVelocity(ItemEntity itemEntity, double x, double y, double z, Operation<Void> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            original.call(itemEntity, x, y, z);
            return;
        }
        
        Vec3d world = RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection);
        GravityChangerAPI.setWorldVelocity(itemEntity, world);
    }

    //TODO: Make sure this works, the ordinal on the return injections was the
    // opposite of what I expected, so make sure it still works as expected

    @ModifyVariable(
            method = "adjustMovementForSneaking",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private Vec3d injected(Vec3d movement) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        return RotationUtil.vecWorldToPlayer(movement, gravityDirection);
    }

    @Inject(
            method = "adjustMovementForSneaking",
            at = @At(value = "RETURN", ordinal = 1),
            cancellable = true
    )
    private void modify_return_if(CallbackInfoReturnable<Vec3d> cir,
                                 @Local(argsOnly = true) Vec3d movement,
                                 @Local(ordinal = 0) double d,
                                 @Local(ordinal = 1) double e) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        cir.setReturnValue(RotationUtil.vecPlayerToWorld(d, movement.y, e, gravityDirection));
    }

    @Inject(
            method = "adjustMovementForSneaking",
            at = @At(value = "RETURN", ordinal = 0),
            cancellable = true
    )
    private void modify_return_else(CallbackInfoReturnable<Vec3d> cir, @Local(argsOnly = true) Vec3d movement) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        cir.setReturnValue(RotationUtil.vecPlayerToWorld(movement, gravityDirection));
    }

    //Might break with small enough scales using pehkui,
    // but at that scale this will be your last problem
    @Redirect(
            method = "isSpaceAroundPlayerEmpty", //isSpaceAroundPlayerEmpty
            at = @At(
                    value = "NEW",
                    target = "(DDDDDD)Lnet/minecraft/util/math/Box;"
            )
    )
    private Box redirect_isSpaceAroundPlayerEmpty_new_box(
            double x1, double y1, double z1, double x2, double y2, double z2,
            @Local(ordinal = 0, argsOnly = true) double offsetX,
            @Local(ordinal = 1, argsOnly = true) double offsetZ,
            @Local(ordinal = 0, argsOnly = true) float offsetY,
            @Local(ordinal=0) Box box
    ) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        double margin = 1.0E-7;
        Vec3d offsets = RotationUtil.vecPlayerToWorld(offsetX, -(offsetY + margin * 2), offsetZ, gravityDirection);
        return new Box(
                box.minX + offsets.x + margin, box.minY + offsets.y + margin, box.minZ + offsets.z + margin,
                box.maxX + offsets.x - margin, box.maxY + offsets.y - margin, box.maxZ + offsets.z - margin);
    }
    
    @WrapOperation(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
            ordinal = 0
        )
    )
    private float wrapOperation_attack_getYaw_0(PlayerEntity attacker, Operation<Float> original, Entity target) {
        Direction targetGravityDirection = GravityChangerAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return original.call(attacker);
        }
        
        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }
    
    @WrapOperation(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
            ordinal = 1
        )
    )
    private float wrapOperation_attack_getYaw_1(PlayerEntity attacker, Operation<Float> original, Entity target) {
        Direction targetGravityDirection = GravityChangerAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return original.call(attacker);
        }
        
        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }
    
    @WrapOperation(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
            ordinal = 2
        )
    )
    private float wrapOperation_attack_getYaw_2(PlayerEntity attacker, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }
        
        return RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getPitch(), gravityDirection).x;
    }
    
    @WrapOperation(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
            ordinal = 3
        )
    )
    private float wrapOperation_attack_getYaw_3(PlayerEntity attacker, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }
        
        return RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getPitch(), gravityDirection).x;
    }

    //TODO: Rotate Death Particles
    
    @ModifyArgs(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"
        )
    )
    private void modify_tickMovement_expand_0(Args args) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        
        Vec3d vec3d = RotationUtil.maskPlayerToWorld(args.get(0), args.get(1), args.get(2), gravityDirection);
        args.set(0, vec3d.x);
        args.set(1, vec3d.y);
        args.set(2, vec3d.z);
    }

    @WrapOperation(
        method = "canChangeIntoPose",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityDimensions;getBoxAt(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Box;"
        )
    )
    private Box wrapOperation_canChangeIntoPose_getBoundingBox(EntityDimensions dimensions, Vec3d pos, Operation<Box> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return original.call(dimensions, pos);
        }

        Box box = dimensions.getBoxAt(0, 0, 0);
        //Box box = original.call(dimensions, pos).offset(pos.negate());
        if (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        return RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(pos);

    }
}
