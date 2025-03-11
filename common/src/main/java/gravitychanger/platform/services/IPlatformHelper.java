package gravitychanger.platform.services;

import gravitychanger.api.ILevelGravityData;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.network.C2SModPacket;
import gravitychanger.network.S2CModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    IEntityGravityData getGravityData(Entity entity);

    ILevelGravityData getLevelGravityData(Level level);


    <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader);

    <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader);


    void sendToClient(S2CModPacket msg, ServerPlayer player);

    void sendToServer(C2SModPacket msg);

    void sendToTracking(S2CModPacket msg, Entity entity, boolean includeSelf);

}