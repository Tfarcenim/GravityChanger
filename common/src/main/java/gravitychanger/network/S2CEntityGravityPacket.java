package gravitychanger.network;

import gravitychanger.ClientPacketHandler;
import gravitychanger.GravityChanger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;

public record S2CEntityGravityPacket(int entityID,CompoundTag data) implements S2CModPacket{

    public static final StreamCodec<RegistryFriendlyByteBuf,S2CEntityGravityPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,S2CEntityGravityPacket::entityID,ByteBufCodecs.COMPOUND_TAG,S2CEntityGravityPacket::data,
            S2CEntityGravityPacket::new
    );

    public static final Type<S2CEntityGravityPacket> TYPE = new Type<>(GravityChanger.id("entity"));


    public S2CEntityGravityPacket(Entity about, CompoundTag data) {
        this(about.getId(),data);
    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
