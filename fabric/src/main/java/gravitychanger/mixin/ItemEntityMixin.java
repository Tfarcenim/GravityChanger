package gravitychanger.mixin;

import gravitychanger.api.GravityChangerAPIFabric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }
    
    @ModifyConstant(method = "tick()V", constant = @Constant())
    private double multiplyGravity(double constant) {
        return constant * GravityChangerAPIFabric.getGravityStrength(this);
    }
}
