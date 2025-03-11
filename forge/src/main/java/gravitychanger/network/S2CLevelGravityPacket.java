package gravitychanger.network;

import gravitychanger.ClientPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class S2CLevelGravityPacket implements S2CModPacket{

    public final CompoundTag data;

    public S2CLevelGravityPacket(FriendlyByteBuf buf) {
        data = buf.readNbt();
    }

    public S2CLevelGravityPacket(CompoundTag data) {
        this.data = data;
    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeNbt(data);
    }
}
