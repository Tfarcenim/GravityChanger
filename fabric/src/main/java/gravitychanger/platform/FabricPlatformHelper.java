package gravitychanger.platform;

import gravitychanger.api.GravityChangerAPIFabric;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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

    @Override
    public ILevelGravityData getLevelGravityData(Level level) {
        return GravityChangerAPIFabric.getLevelGravityComponent(level);
    }
}
