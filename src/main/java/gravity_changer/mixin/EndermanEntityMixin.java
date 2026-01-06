package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin {
    //The 3 isPlayerStaring mixins check the PLAYER's gravity only, so any rotation player can look at a DOWN enderman
    // and it will work, but enderman of different orientations do not work even when player is DOWN
    //TODO: Implement the missing case described above, and make sure both cases work together
    //TODO: Particles are also not rotated
    @Redirect(
        method = "isPlayerStaring(Lnet/minecraft/entity/player/PlayerEntity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getEyeY()D",
            ordinal = 0
        )
    )
    private double redirect_isPlayerStaring_getEyeY_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getEyeY();
        }
        
        return playerEntity.getEyePos().y;
    }
    
    @Redirect(
        method = "isPlayerStaring(Lnet/minecraft/entity/player/PlayerEntity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getX()D",
            ordinal = 0
        )
    )
    private double redirect_isPlayerStaring_getX_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getX();
        }
        
        return playerEntity.getEyePos().x;
    }
    
    @Redirect(
        method = "isPlayerStaring(Lnet/minecraft/entity/player/PlayerEntity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_isPlayerStaring_getZ_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getZ();
        }
        
        return playerEntity.getEyePos().z;
    }
    
    @Redirect(
        method = "teleportTo(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getEyeY()D",
            ordinal = 0
        )
    )
    private double redirect_teleportTo_getEyeY_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getEyeY();
        }
        
        return entity.getEyePos().y;
    }
    
    @Redirect(
        method = "teleportTo(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getX()D",
            ordinal = 0
        )
    )
    private double redirect_teleportTo_getX_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getX();
        }
        
        return entity.getEyePos().x;
    }
    
    @Redirect(
        method = "teleportTo(Lnet/minecraft/entity/Entity;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getZ()D",
            ordinal = 0
        )
    )
    private double redirect_teleportTo_getZ_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getZ();
        }
        
        return entity.getEyePos().z;
    }
}
