package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPIFabric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LlamaSpit.class)
public class LlamaSpitMixin {
    @ModifyArg(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;applyGravity()V"
        ),
        index = 1
    )
    private double multiplyGravity(double x) {
        return x * GravityChangerAPIFabric.getGravityStrength(((Entity) (Object) this));
    }
}
