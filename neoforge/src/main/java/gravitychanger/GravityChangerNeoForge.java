package gravitychanger;

import gravitychanger.api.GravityChangerAPI;
import gravitychanger.command.GravityCommand;
import gravitychanger.network.PacketHandlerNeoForge;
import gravitychanger.network.S2CEntityGravityPacket;
import gravitychanger.platform.Services;
import gravitychanger.util.EntityGravityData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(GravityChanger.MOD_ID)
public class GravityChangerNeoForge {

    public GravityChangerNeoForge(IEventBus bus) {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        bus.addListener(this::registerCaps);
        bus.addListener(this::setup);
        bus.addListener(this::register);
        bus.addListener(PacketHandlerNeoForge::register);
        // Use Forge to bootstrap the Common mod.
        GravityChanger.init();
        //NeoForge.EVENT_BUS.addGenericListener(Entity.class,this::attachEntity);
        //NeoForge.EVENT_BUS.addGenericListener(Level.class,this::attachLevel);
        NeoForge.EVENT_BUS.addListener(this::login);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::tracking);
        NeoForge.EVENT_BUS.addListener(this::respawn);
        NeoForge.EVENT_BUS.addListener(this::dimensionChange);
        NeoForgeEvents.init();
    }

    void register(RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.BLOCK) {
            GravityChanger.register();
        }
    }

    void setup(FMLCommonSetupEvent event) {
    }

    void login(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();
        //                Services.PLATFORM.sendToTracking(new S2CSyncEntityGravityPacket(entity, serializeNBT()), entity, true);

        /*level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data), player);
        });*/

        EntityGravityData entityGravityData = GravityChangerAPI.getGravityData(player);
        CompoundTag data = new CompoundTag();
        entityGravityData.toNbt(data);
        Services.PLATFORM.sendToTracking(new S2CEntityGravityPacket(player, data), player, true);
    }

    void respawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();

       /* level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
        });*/

        EntityGravityData entityGravityData = GravityChangerAPI.getGravityData(player);

        CompoundTag data = new CompoundTag();
        entityGravityData.toNbt(data);
        Services.PLATFORM.sendToTracking(new S2CEntityGravityPacket(player, data), player, true);
    }


    void dimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.server.getLevel(event.getTo());


        /*level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
        });*/
    }

    void tracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        EntityGravityData targetEntityGravitydata = GravityChangerAPI.getGravityData(target);

        CompoundTag data = new CompoundTag();
        targetEntityGravitydata.toNbt(data);
        Services.PLATFORM.sendToClient(new S2CEntityGravityPacket(target, data),
                (ServerPlayer) player);

    }

    void commands(RegisterCommandsEvent event) {
        GravityCommand.register(event.getDispatcher());
    }


    void registerCaps(RegisterCapabilitiesEvent event) {

    }
}