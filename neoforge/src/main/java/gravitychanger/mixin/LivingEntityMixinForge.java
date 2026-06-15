package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixinForge {

    //modify the gravity attribute
    @ModifyVariable(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private double multiplyGravity(double amount) {
        return amount * GravityChangerAPI.getGravityStrength((Entity)(Object)this);
    }
}
