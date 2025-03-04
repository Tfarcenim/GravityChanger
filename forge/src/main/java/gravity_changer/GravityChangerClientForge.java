package gravity_changer;

import gravity_changer.api.GravityChangerAPIForge;
import gravity_changer.network.S2CSyncEntityGravityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class GravityChangerClientForge {


    public static void handle(S2CSyncEntityGravityPacket s2CSyncEntityGravityPacket) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(s2CSyncEntityGravityPacket.entityID);
            if (entity != null) {
                GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().ifPresent(entityGravityAttachment ->
                        entityGravityAttachment.deserializeNBT(s2CSyncEntityGravityPacket.data));
            }
        }
    }
}
