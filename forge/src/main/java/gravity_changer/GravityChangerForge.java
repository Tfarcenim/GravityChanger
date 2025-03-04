package gravity_changer;

import gravity_changer.api.GravityChangerAPIForge;
import gravity_changer.capability.DimensionGravity;
import gravity_changer.capability.EntityGravity;
import gravity_changer.capability.EntityGravityAttachment;
import gravity_changer.command.ArgumentTypes;
import gravity_changer.command.DirectionArgumentType;
import gravity_changer.command.GravityCommand;
import gravity_changer.command.LocalDirectionArgumentType;
import gravity_changer.item.GravityAnchorItem;
import gravity_changer.network.S2CSyncEntityGravityPacket;
import gravity_changer.platform.Services;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
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
        bus.addListener(this::register);
        bus.addListener(this::setup);
        // Use Forge to bootstrap the Common mod.
        GravityChanger.init();
        Services.PLATFORM.registerClientPacket(S2CSyncEntityGravityPacket.class,S2CSyncEntityGravityPacket::new);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,this::attachEntityCaps);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class,this::attachLevelCaps);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
    }

    void commands(RegisterCommandsEvent event) {
        GravityCommand.register(event.getDispatcher());
    }

    void register(RegisterEvent event) {

        event.register(Registries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("direction"),
                () -> {
                    SingletonArgumentInfo<DirectionArgumentType> info = SingletonArgumentInfo.contextFree(() -> ArgumentTypes.DIRECTION);
                    ArgumentTypeInfos.registerByClass(DirectionArgumentType.class,info);
                    return info;
        });

        event.register(Registries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("local_direction"),
                () -> {
                    SingletonArgumentInfo<LocalDirectionArgumentType> info = SingletonArgumentInfo.contextFree(() -> ArgumentTypes.LOCAL_DIRECTION);
                    ArgumentTypeInfos.registerByClass(LocalDirectionArgumentType.class,info);
                    return info;
                });

        for (Direction direction : Direction.values()) {
            event.register(Registries.ITEM, GravityAnchorItem.getItemId(direction),() -> GravityAnchorItem.ITEM_MAP.get(direction));
        }

    }

    void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        Entity e = event.getObject();
        if (EntityTags.canChangeGravity(e)) {
            event.addCapability(GravityChanger.id("entity_gravity"),new EntityGravity(e));
        }
    }

    void attachLevelCaps(AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
            event.addCapability(GravityChanger.id("dimension_gravity"),new DimensionGravity());
    }

    void setup(FMLCommonSetupEvent event) {
        ModEventHandler.init();

    }

    public static void onEntityTick(Entity entity) {
        GravityChangerAPIForge.getEntityGravityAttachment(entity).resolve().ifPresent(EntityGravityAttachment::tick);
    }
}