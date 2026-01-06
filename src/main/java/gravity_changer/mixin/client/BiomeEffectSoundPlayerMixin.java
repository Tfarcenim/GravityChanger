package gravity_changer.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//method_26271 refers to a lambda which is why this class may cause mixin warnings/errors
@Mixin(BiomeEffectSoundPlayer.class)
public abstract class BiomeEffectSoundPlayerMixin {

    //todo: Probably fine, ignoring it tho
    @Redirect(
        method = "method_26271",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEyeY()D"
        )
    )
    private double redirect_method_26271_getEyeY_0(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.getEyePos().y;
    }
    
    @Redirect(
        method = "method_26271",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"
        )
    )
    private double redirect_method_26271_getX_0(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.getEyePos().x;
    }
    
    @Redirect(
        method = "method_26271",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"
        )
    )
    private double redirect_method_26271_getZ_0(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.getEyePos().z;
    }
}
