package gravity_changer;

import gravity_changer.plating.GravityPlatingBlock;
import gravity_changer.util.GCUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class GravityChangerModClient implements ClientModInitializer {
    private static final String ISSUE_LINK = "https://github.com/Magicalbananapi/GravityChanger/issues";
    private static boolean displayPreviewWarning = true;
    
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(new ClientTickEvents.StartTick() {
            @Override
            public void onStartTick(MinecraftClient client) {
                if (client.player == null) {
                    return;
                }
                if (displayPreviewWarning) {
                    displayPreviewWarning = false;
                    client.player.sendMessage(
                        Text.translatable("gravity_changer.preview").append(
                            GCUtil.getLinkText(ISSUE_LINK)
                        )
                    );
                }
            }
        });
        
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), GravityPlatingBlock.PLATING_BLOCK);
        
    }
}
