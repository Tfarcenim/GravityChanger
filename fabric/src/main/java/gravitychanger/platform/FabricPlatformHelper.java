package gravitychanger.platform;

import gravitychanger.api.GravityChangerAPIFabric;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.network.C2SModPacket;
import gravitychanger.network.S2CModPacket;
import gravitychanger.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

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

    ///////never used
    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {

    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {

    }

    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {

    }

    @Override
    public void sendToServer(C2SModPacket msg) {

    }

    @Override
    public void sendToTracking(S2CModPacket msg, Entity entity, boolean includeSelf) {

    }
}
