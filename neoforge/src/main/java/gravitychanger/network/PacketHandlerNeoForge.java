package gravitychanger.network;

import gravitychanger.GravityChanger;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandlerNeoForge {
    public static PayloadRegistrar registrar;
    public static void register(RegisterPayloadHandlersEvent event) {
        registrar = event.registrar(GravityChanger.MOD_ID);
        PacketHandler.registerPackets();
    }


    public static <MSG extends S2CModPacket> void sendToClient(MSG packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,packet);
    }

    public static <MSG extends C2SModPacket> void sendToServer(MSG packet) {
        PacketDistributor.sendToServer(packet);
    }

}
