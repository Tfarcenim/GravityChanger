package gravity_changer.platform;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.network.ClientPacketHandlerFabric;
import gravity_changer.network.PacketHandler;
import gravity_changer.network.PacketHandlerFabric;
import gravity_changer.network.client.S2CModPacket;
import gravity_changer.network.server.C2SModPacket;
import gravity_changer.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;
import java.util.Collection;
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
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        if (MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.CLIENT) {
            ClientPacketHandlerFabric.register(packetLocation,reader);
        }
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        ServerPlayNetworking.registerGlobalReceiver(PacketHandler.packet(packetLocation), PacketHandlerFabric.wrapC2S(reader));
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.write(buf);
        ServerPlayNetworking.send(player, PacketHandler.packet(msg.getClass()), buf);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.write(buf);
        ClientPlayNetworking.send(PacketHandler.packet(msg.getClass()), buf);
    }

    @Override
    public void sendToTracking(S2CModPacket msg, Entity entity) {
        Collection<ServerPlayer> tracking = new ArrayList<>(PlayerLookup.tracking(entity));
        if (entity instanceof ServerPlayer self) {
            sendToClient(msg,self);
        }
        for (ServerPlayer player : tracking) {
            sendToClient(msg,player);
        }
    }

    @Override
    public double getLevelGravity(Level level) {
        return GravityChangerAPI.DIMENSION_DATA_COMPONENT.get(level).getDimensionGravityStrength();
    }
}
