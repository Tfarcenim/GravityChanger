package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
public class BoatMixin {
    @ModifyConstant(method = "floatBoat()V", constant = @Constant())
    private double multiplyGravity(double constant) {
        return constant * GravityChangerAPI.getGravityStrength(((Entity) (Object) this));
    }
}
