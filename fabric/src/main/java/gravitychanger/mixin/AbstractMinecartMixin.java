package gravitychanger.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity {
    public AbstractMinecartMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    
//    @ModifyArg(
//        method = "tick",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;applyGravity()V"
//        ),
//        index = 1
//    )
//    private double multiplyGravity(double x) {
//        return x * GravityChangerAPI.getGravityStrength(this);
//    }
}
