package gravitychanger;

import gravitychanger.command.GravityCommand;
import gravitychanger.init.ModCreativeTabs;
import gravitychanger.plating.GravityPlatingBlock;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class GravityChangerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricEvents.init();

        GravityChanger.init();

        
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, GravityChanger.id("general"),
                ModCreativeTabs.GENERAL
        );

        Registry.register(
                BuiltInRegistries.BLOCK, GravityChanger.id("gravity_plating"), GravityPlatingBlock.PLATING_BLOCK
        );

        Registry.register(
                BuiltInRegistries.ITEM, GravityChanger.id("gravity_plating"),
                GravityPlatingItem.PLATING_BLOCK_ITEM
        );
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GravityChanger.id("gravity_plating"), GravityPlatingBlockEntity.TYPE);
    }
}
