package gravitychanger;

import gravitychanger.plating.GravityPlatingBlock;
import gravitychanger.util.GCUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class GravityChangerModClient implements ClientModInitializer {
    private static final String ISSUE_LINK = "https://github.com/NightEpiphany/gravity-changer-1.21.1/issues";
    private static final String MODRINTH_LINK = "https://modrinth.com/mod/gravity-changer-unofficial-port";
    private static boolean displayPreviewWarning = true;
    
    @Override
    public void onInitializeClient() {
        if (GravityChangerMod.config.showInfoOnStartUp)
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if (client.player == null) {
                    return;
                }
                if (displayPreviewWarning) {
                    displayPreviewWarning = false;
                    client.player.sendSystemMessage(
                        Component.translatable("gravity_changer.preview")
                                .append(GCUtil.getLinkText(MODRINTH_LINK).withStyle(ChatFormatting.UNDERLINE))
                                //.append("\n")
                                //.append(GCUtil.getLinkText(ISSUE_LINK).withStyle(ChatFormatting.UNDERLINE))
                    );
                }
            });
        
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), GravityPlatingBlock.PLATING_BLOCK);
        
    }
}
