package gravitychanger;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.GravityCommand;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.init.ModCreativeTabs;
import gravitychanger.init.ModItems;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import gravitychanger.mob_effect.refined.GravityPotions;
import gravitychanger.mob_effect.refined.GravityStrengthMobEffect;
import gravitychanger.network.S2CEntityGravityPacket;
import gravitychanger.network.S2CLevelGravityPacket;
import gravitychanger.platform.Services;
import gravitychanger.plating.GravityPlatingBlock;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
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

    public static void onTick(Entity entity) {
        //todo entity.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(IEntityGravityData::commonTick);
    }

    void register(RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.BLOCK) {
            GravityChanger.register();
        }


    }

    void setup(FMLCommonSetupEvent event) {
        Services.PLATFORM.registerClientPacket(S2CEntityGravityPacket.class,S2CEntityGravityPacket::new);
        Services.PLATFORM.registerClientPacket(S2CLevelGravityPacket.class,S2CLevelGravityPacket::new);
    }

    void login(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();
        //                Services.PLATFORM.sendToTracking(new S2CSyncEntityGravityPacket(entity, serializeNBT()), entity, true);

        level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
        });

        player.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToTracking(new S2CEntityGravityPacket(player,data),player,true);
        });
    }

    void respawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();

        level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
        });

        player.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToTracking(new S2CEntityGravityPacket(player,data),player,true);
        });
    }


    void dimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.server.getLevel(event.getTo());
        level.getCapability(GravityChangerAPIForge.LEVEL_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CLevelGravityPacket(data),player);
        });
    }

    void tracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        target.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToClient(new S2CEntityGravityPacket(target,data),
                    (ServerPlayer) player);
        });

    }

    void commands(RegisterCommandsEvent event) {
        GravityCommand.register(event.getDispatcher());
    }


    void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IEntityGravityData.class);
        event.register(ILevelGravityData.class);
    }

    void attachEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        event.addCapability(GravityChanger.DATA_COMPONENT_ID,new EntityGravityCapability(entity));
    }

    void attachLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        event.addCapability(GravityChanger.DIMENSION_DATA_ID,new LevelGravityCapability(level));
    }
}