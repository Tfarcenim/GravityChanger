package gravitychanger.network;

import gravitychanger.ClientPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CLevelGravityPacket(CompoundTag data) implements S2CModPacket {

    public S2CLevelGravityPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
