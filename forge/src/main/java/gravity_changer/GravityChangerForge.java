package gravity_changer;

import net.minecraftforge.fml.common.Mod;

@Mod(GravityChanger.MOD_ID)
public class GravityChangerForge {
    
    public GravityChangerForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        GravityChanger.init();
        
    }
}