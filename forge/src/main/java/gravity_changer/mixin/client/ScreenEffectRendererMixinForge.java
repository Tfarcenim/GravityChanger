package gravity_changer.mixin.client;

import gravity_changer.ClientHooksForge;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixinForge {
    @Inject(
        method = "getOverlayBlock",//note, this method is added in by forge
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private static void inject_getInWallBlockState(Player player, CallbackInfoReturnable<Pair<BlockState, BlockPos>> cir) {
        ClientHooksForge.inject_getInWallBlockState(player, cir);
    }
}
