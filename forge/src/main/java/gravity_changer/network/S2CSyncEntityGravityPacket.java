package gravity_changer.network;

import gravity_changer.GravityChangerClientForge;
import gravity_changer.network.client.S2CModPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class S2CSyncEntityGravityPacket implements S2CModPacket {

    public final int entityID;
    public final CompoundTag data;

    public S2CSyncEntityGravityPacket(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        data = buf.readNbt();
    }

    public S2CSyncEntityGravityPacket(Entity about,CompoundTag tag) {
        entityID = about.getId();
        data=tag;

    }

    @Override
    public void handleClient() {
        GravityChangerClientForge.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
        to.writeNbt(data);
    }
}
