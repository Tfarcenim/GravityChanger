package gravitychanger;

import net.minecraftforge.fml.common.Mod;

@Mod(GravityChanger.MOD_ID)
public class ExampleMod {
    
    public ExampleMod() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        GravityChanger.LOG.info("Hello Forge world!");
        GravityChanger.init();
        
    }
}