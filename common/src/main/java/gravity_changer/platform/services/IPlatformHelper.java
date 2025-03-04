package gravity_changer.platform.services;

import com.mojang.brigadier.context.CommandContext;
import gravity_changer.RotationAnimation;
import gravity_changer.api.RotationParameters;
import gravity_changer.network.client.S2CModPacket;
import gravity_changer.network.server.C2SModPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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

    <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);
    <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);
    void sendToClient(S2CModPacket msg, ServerPlayer player);
    void sendToServer(C2SModPacket msg);
    void sendToTracking(S2CModPacket msg, Entity entity,boolean includeSelf);

    //gravity helpers

    double getLevelGravity(Level level);
    void setLevelGravity(Level world, double strength);

    Direction getGravityDirection(Entity entity);

    double getGravityStrength(Entity entity);

    double getBaseGravityStrength(Entity entity);

    void setBaseGravityStrength(Entity entity, double strength);

    void instantlySetClientBaseGravityDirection(Entity entity, Direction direction);
    /**
     * Returns the main gravity direction for the given entity
     * This may not be the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
     Direction getBaseGravityDirection(Entity entity);

    void setBaseGravityDirection(
            Entity entity, Direction gravityDirection
    );

    void resetGravity(Entity entity);

    int viewGravity(CommandContext<CommandSourceStack> ctx);

    @Nullable
    RotationAnimation getRotationAnimation(Entity entity);

    void  applyGravityDirectionEffect(Entity entity,Direction gravityEffectDir,  @Nullable RotationParameters rotationParameters,
             double priority);

}