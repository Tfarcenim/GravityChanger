package gravity_changer;

import gravity_changer.command.ArgumentTypes;
import gravity_changer.command.DirectionArgumentType;
import gravity_changer.command.LocalDirection;
import gravity_changer.command.LocalDirectionArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
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
    }

    void setup(FMLCommonSetupEvent event) {
    }
}