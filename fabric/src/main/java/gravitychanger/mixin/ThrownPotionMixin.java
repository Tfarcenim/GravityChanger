package gravitychanger.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gravitychanger.api.GravityChangerAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownPotion.class)
public class ThrownPotionMixin {
    @ModifyReturnValue(method = "getDefaultGravity", at = @At("RETURN"))
    private double multiplyGravity(double original) {
        return original * (float) GravityChangerAPI.getGravityStrength(((Entity) (Object) this));
    }
}
