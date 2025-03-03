package gravity_changer.network.client;

import net.minecraft.network.FriendlyByteBuf;
import tfar.customabilities.attachments.CommonDataAttachment;
import tfar.customabilities.attachments.CommonDataAttachments;
import tfar.customabilities.client.ClientPacketHandler;

public abstract class S2CSyncDataAttachmentPacket<T> implements S2CModPacket{

    public int entityID;
    public CommonDataAttachment<T> attachment;
    public T value;

    public S2CSyncDataAttachmentPacket(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        attachment = (CommonDataAttachment<T>) CommonDataAttachments.MAP.get(buf.readResourceLocation());
        value = readValue(buf);
    }

    protected abstract T readValue(FriendlyByteBuf buf);
    protected abstract void writeValue(FriendlyByteBuf buf);

    public S2CSyncDataAttachmentPacket(int entityID, CommonDataAttachment<T> attachment, T value) {
        this.entityID = entityID;
        this.attachment = attachment;
        this.value = value;
    }

    @Override
    public void handleClient() {
        ClientPacketHandler.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(entityID);
        to.writeResourceLocation(attachment.getName());
        writeValue(to);
    }
}
