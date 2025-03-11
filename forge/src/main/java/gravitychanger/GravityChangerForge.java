package gravitychanger;

import gravitychanger.api.GravityChangerAPIForge;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.ILevelGravityData;
import gravitychanger.command.GravityCommand;
import gravitychanger.network.S2CEntityGravityPacket;
import gravitychanger.platform.Services;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GravityChanger.MOD_ID)
public class GravityChangerForge {
    
    public GravityChangerForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        bus.addListener(this::registerCaps);
        // Use Forge to bootstrap the Common mod.
        GravityChanger.init();
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,this::attachEntity);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class,this::attachLevel);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::tracking);
    }

    void login(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        //                Services.PLATFORM.sendToTracking(new S2CSyncEntityGravityPacket(entity, serializeNBT()), entity, true);
        player.getCapability(GravityChangerAPIForge.ENTITY_GRAVITY).ifPresent(entityGravityAttachment -> {
            CompoundTag data = new CompoundTag();
            entityGravityAttachment.toNbt(data);
            Services.PLATFORM.sendToTracking(new S2CEntityGravityPacket(player,data),player,true);
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