package gravitychanger.platform;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.network.C2SModPacket;
import gravitychanger.network.PacketHandlerForge;
import gravitychanger.network.S2CModPacket;
import gravitychanger.platform.services.IPlatformHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    @Nullable
    public IEntityGravityData getGravityData(Entity entity) {
        return entity.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).orElse(null);
    }

    @Override
    public ILevelGravityData getLevelGravityData(Level level) {
        return level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).orElse(null);
    }

    int i;

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapS2C());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapC2S());
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketHandlerForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketHandlerForge.sendToServer(msg);
    }

    @Override
    public void sendToTracking(S2CModPacket msg, Entity entity, boolean includeSelf) {
        if (includeSelf) {
            PacketHandlerForge.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),msg);
        } else {
            PacketHandlerForge.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),msg);
        }
    }
}