package gravitychanger.mixin.client;

import gravitychanger.GravityChangerClient;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixinFabric {
    @Inject(
            method = "getViewBlockingState(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void inject_getInWallBlockState(Player player, CallbackInfoReturnable<BlockState> cir) {
        GravityChangerClient.inject_getInWallBlockState(player, cir);
    }
}
