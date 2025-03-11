package gravitychanger.platform;

import gravitychanger.api.GravityChangerAPIFabric;
import gravitychanger.api.IEntityGravityData;
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
    public IEntityGravityData getGravityData(Entity entity) {
        return GravityChangerAPIFabric.getGravityComponent(entity);
    }
}
