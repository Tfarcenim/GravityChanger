package gravity_changer.platform;

import gravity_changer.api.GravityChangerAPIForge;
import gravity_changer.capability.DimensionDataAttachment;
import gravity_changer.network.PacketHandlerForge;
import gravity_changer.network.client.S2CModPacket;
import gravity_changer.network.server.C2SModPacket;
import gravity_changer.platform.services.IPlatformHelper;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    int i;

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapS2C());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapC2S());
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketHandlerForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketHandlerForge.sendToServer(msg);
    }

    @Override
    public void sendToTracking(S2CModPacket msg, Entity entity) {

    }

    @Override
    public double getLevelGravity(Level level) {
        return GravityChangerAPIForge.getOptional(level).resolve().map(DimensionDataAttachment::getDimensionGravityStrength).orElse(1d);
    }

    @Override
    public void setLevelGravity(Level world, double strength) {
        GravityChangerAPIForge.getOptional(world).resolve().ifPresent(dimensionDataAttachment -> dimensionDataAttachment.setDimensionGravityStrength(strength));
    }

    @Override
    public Direction getGravityDirection(Entity entity) {
        return null;
    }

    @Override
    public double getGravityStrength(Entity entity) {
        return 0;
    }

    @Override
    public double getBaseGravityStrength(Entity entity) {
        return 0;
    }

    @Override
    public void setBaseGravityStrength(Entity entity, double strength) {

    }

    @Override
    public void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {

    }


}