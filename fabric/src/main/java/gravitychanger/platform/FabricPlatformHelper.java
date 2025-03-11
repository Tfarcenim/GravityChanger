package gravitychanger.platform;

import gravitychanger.api.GravityChangerAPIFabric;
import gravitychanger.api.IGravityData;
import gravitychanger.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IGravityData getGravityData(Entity entity) {
        return GravityChangerAPIFabric.getGravityComponent(entity);
    }
}
