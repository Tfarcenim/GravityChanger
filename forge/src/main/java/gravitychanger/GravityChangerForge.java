package gravitychanger;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.GravityCommand;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.network.S2CEntityGravityPacket;
import gravitychanger.network.S2CLevelGravityPacket;
import gravitychanger.platform.Services;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(GravityChanger.MOD_ID)
public class GravityChangerForge {
    
    public GravityChangerForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        bus.addListener(this::registerCaps);
        bus.addListener(this::setup);
        bus.addListener(this::register);
        // Use Forge to bootstrap the Common mod.
        GravityChanger.init();
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,this::attachEntity);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class,this::attachLevel);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::tracking);
        MinecraftForge.EVENT_BUS.addListener(this::respawn);
        MinecraftForge.EVENT_BUS.addListener(this::dimensionChange);
        ForgeEvents.init();
    }

    public static void onTick(Entity entity) {
        entity.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(IEntityGravityData::commonTick);
    }

    void register(RegisterEvent event) {

        event.register(Registries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("direction"),
                () -> {
                    SingletonArgumentInfo<DirectionArgumentType> info = SingletonArgumentInfo.contextFree(() -> DirectionArgumentType.instance);
                    ArgumentTypeInfos.registerByClass(DirectionArgumentType.class,info);
                    return info;
                });

        event.register(Registries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("local_direction"),
                () -> {
                    SingletonArgumentInfo<LocalDirectionArgumentType> info = SingletonArgumentInfo.contextFree(() -> LocalDirectionArgumentType.instance);
                    ArgumentTypeInfos.registerByClass(LocalDirectionArgumentType.class,info);
                    return info;
                });


        for (Direction direction : Direction.values()) {
            event.register(
                    Registries.ITEM,  GravityChanger.id( "gravity_anchor_" + direction.getName()),() -> GravityAnchorItem.ITEM_MAP.get(direction)
            );
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
        if (EntityTags.canChangeGravity(entity)) {
            event.addCapability(GravityChanger.id("entity_data"),new EntityGravityCapability(entity));
        }
    }

    void attachLevel(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        event.addCapability(GravityChanger.id("level_data"),new LevelGravityCapability(level));
    }
}