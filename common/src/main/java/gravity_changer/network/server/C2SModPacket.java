package gravity_changer.network.server;

import gravity_changer.network.ModPacket;
import net.minecraft.server.level.ServerPlayer;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
