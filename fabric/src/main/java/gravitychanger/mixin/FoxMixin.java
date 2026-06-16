package gravitychanger.mixin;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Fox.class)
public class FoxMixin {
//    @ModifyVariable(method = "Lnet/minecraft/world/entity/animal/Fox;calculateFallDamage(FF)I", at = @At("HEAD"), ordinal = 0, argsOnly = true)
//    private float diminishFallDamage(float value) {
//        return value * (float) Math.sqrt(GravityChangerAPIFabric.getGravityStrength(((Entity) (Object) this)));
//    }
}
