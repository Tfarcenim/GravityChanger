package gravity_changer.platform;

import com.mojang.brigadier.context.CommandContext;
import gravity_changer.GravityChangerForge;
import gravity_changer.RotationAnimation;
import gravity_changer.api.GravityChangerAPIForge;
import gravity_changer.capability.DimensionAttachment;
import gravity_changer.capability.EntityGravityAttachment;
import gravity_changer.network.PacketHandlerForge;
import gravity_changer.network.client.S2CModPacket;
import gravity_changer.network.server.C2SModPacket;
import gravity_changer.platform.services.IPlatformHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

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
    public void sendToTracking(S2CModPacket msg, Entity entity, boolean includeSelf) {
        if (includeSelf) {
            PacketHandlerForge.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),msg);
        } else {
            PacketHandlerForge.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),msg);
        }
    }

    @Override
    public double getLevelGravity(Level level) {
        return GravityChangerAPIForge.getDimensionAttachment(level).resolve().map(DimensionAttachment::getDimensionGravityStrength).orElse(1d);
    }

    @Override
    public void setLevelGravity(Level world, double strength) {
        GravityChangerAPIForge.getDimensionAttachment(world).resolve().ifPresent(dimensionDataAttachment -> dimensionDataAttachment.setDimensionGravityStrength(strength));
    }

    @Override
    public Direction getGravityDirection(Entity entity) {
        return GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve()
                .map(EntityGravityAttachment::getCurrGravityDirection).orElse(Direction.DOWN);
    }

    @Override
    public double getGravityStrength(Entity entity) {
        return GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve()
                .map(EntityGravityAttachment::getCurrGravityStrength).orElse(1d);
    }

    @Override
    public double getBaseGravityStrength(Entity entity) {
        return GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve()
                .map(EntityGravityAttachment::getBaseGravityStrength).orElse(1d);
    }

    @Override
    public void setBaseGravityStrength(Entity entity, double strength) {
        GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve()
                .ifPresent(entityGravityAttachment -> entityGravityAttachment.setBaseGravityStrength(strength));
    }

    @Override
    public void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {
        GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().ifPresent(entityGravityAttachment -> {
            entityGravityAttachment.setBaseGravityDirection(direction);
            entityGravityAttachment.updateGravityStatus();
            entityGravityAttachment.forceApplyGravityChange();
        });
    }

    @Override
    public Direction getBaseGravityDirection(Entity entity) {
        return GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().map(EntityGravityAttachment::getBaseGravityDirection).orElse(Direction.DOWN);
    }

    @Override
    public void setBaseGravityDirection(Entity entity, Direction gravityDirection) {
        GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().ifPresent(entityGravityAttachment -> entityGravityAttachment.setBaseGravityDirection(gravityDirection));
    }

    @Override
    public void resetGravity(Entity entity) {
        GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().ifPresent(EntityGravityAttachment::reset);
    }

    @Override
    public int viewGravity(CommandContext<CommandSourceStack> ctx) {

        Entity entity = ctx.getSource().getEntity();

        if (entity != null) {
            EntityGravityAttachment component = GravityChangerAPIForge.getEntityGravityAttachment(entity).orElse(null);
            if (component != null) {
                ctx.getSource().sendSuccess(
                        () -> Component.translatable(
                                "gravity_changer.command.inform",
                                component.getBaseGravityDirection().getName(),
                                component.getBaseGravityStrength()
                        ), false
                );
            }
        }
        return 1;
    }

    @Override
    public @Nullable RotationAnimation getRotationAnimation(Entity entity) {
        return GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().map(EntityGravityAttachment::getRotationAnimation).orElse(null);
    }
}