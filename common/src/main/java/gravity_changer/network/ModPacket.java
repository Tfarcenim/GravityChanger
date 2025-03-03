package gravity_changer.network;

import net.minecraft.network.FriendlyByteBuf;

public interface ModPacket {

    void write(FriendlyByteBuf to);

}
