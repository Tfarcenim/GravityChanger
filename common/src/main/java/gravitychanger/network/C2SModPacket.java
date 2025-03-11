package gravitychanger.network;

import net.minecraft.server.level.ServerPlayer;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
