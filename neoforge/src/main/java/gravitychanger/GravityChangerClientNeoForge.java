package gravitychanger;

import gravitychanger.plating.GravityPlatingBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = GravityChanger.MOD_ID,dist = Dist.CLIENT)
public class GravityChangerClientNeoForge {

    public GravityChangerClientNeoForge(IEventBus modBus) {
        modBus.addListener(this::renderlayers);
    }

    void renderlayers(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(GravityPlatingBlock.PLATING_BLOCK, RenderType.cutout());
    }
}
