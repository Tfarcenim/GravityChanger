package gravity_changer.mixin;

import gravity_changer.GravityChangerForge;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixinForge {

    @Inject(method = "baseTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    private void onEntityTick(CallbackInfo ci) {
        GravityChangerForge.onEntityTick((Entity)(Object)this);
    }
}
