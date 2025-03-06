package gravity_changer.network;

import gravity_changer.GravityChangerClientForge;
import gravity_changer.network.client.S2CModPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class S2CSyncDimensionGravityPacket implements S2CModPacket {

    public final double gravity;

    public S2CSyncDimensionGravityPacket(FriendlyByteBuf buf) {
        gravity = buf.readDouble();
    }

    public S2CSyncDimensionGravityPacket(double gravity) {
        this.gravity = gravity;
    }

    @Override
    public void handleClient() {
        GravityChangerClientForge.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeDouble(gravity);
    }
}
