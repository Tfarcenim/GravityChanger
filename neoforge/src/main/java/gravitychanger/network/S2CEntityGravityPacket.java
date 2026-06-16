package gravitychanger.network;

import gravitychanger.ClientPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;

public class S2CEntityGravityPacket implements S2CModPacket{

    public final int entityID;
    public final CompoundTag data;

    public S2CEntityGravityPacket(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        data = buf.readNbt();
    }

    public S2CEntityGravityPacket(Entity about, CompoundTag data) {
        this.entityID = about.getId();
        this.data = data;
    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
        to.writeNbt(data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
