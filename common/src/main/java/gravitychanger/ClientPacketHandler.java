package gravitychanger;

import gravitychanger.api.GravityChangerAPI;import gravitychanger.network.S2CEntityGravityPacket;
import gravitychanger.network.S2CLevelGravityPacket;
import gravitychanger.util.EntityGravityData;import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class ClientPacketHandler {
    public static void handle(S2CLevelGravityPacket s2CLevelGravityPacket) {
        ClientLevel level = Minecraft.getInstance().level;
        //level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(iLevelGravityData -> iLevelGravityData.fromNbt(s2CLevelGravityPacket.data()));
    }

    public static void handle(S2CEntityGravityPacket s2CEntityGravityPacket) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(s2CEntityGravityPacket.entityID());
        if (entity != null) {

            EntityGravityData entityGravityData = GravityChangerAPI.getGravityData(entity);

            entityGravityData.fromNbt(s2CEntityGravityPacket.data());
            entityGravityData.applyGravityChange();
        }
    }
}
