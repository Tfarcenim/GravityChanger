package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    
    @Shadow
    private @Nullable LivingEntity shooter;

    public FireworkRocketEntityMixin(EntityType<?> type, World world) { super(type, world); }

    //TODO: Verify if this changes the value on creation or once it's set, if its just on creation
    // its immediately thrown away
    @ModifyVariable(
        method = "tick()V",
        at = @At(value = "STORE"), ordinal = 0
    )
    public Vec3d tick(Vec3d value) {
        if (shooter != null) {
            value = RotationUtil.vecWorldToPlayer(value, GravityChangerAPI.getGravityDirection(shooter));
        }
        return value;
    }
}
