package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPIFabric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @ModifyArg(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ExperienceOrb;applyGravity()V"
        ),
        index = 1
    )
    private double multiplyGravity(double x) {
        return x * GravityChangerAPIFabric.getGravityStrength(((Entity) (Object) this));
    }
}
