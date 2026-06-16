package gravitychanger;

import gravitychanger.api.IEntityGravityData;
import gravitychanger.command.GravityCommand;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.item.GravityChangerItem;
import gravitychanger.item.GravityChangerItemAOE;
import gravitychanger.mob_effect.refined.GravityStrengthStatusEffect;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class GravityChangerFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        GravityChanger.register();

        GravityChanger.LOG.info("[gravity-changer]");

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );

//        GravityDirectionMobEffect.registerEffects();
//        GravityDirectionMobEffect.registerEvent();
//        GravityInvertMobEffect.init();
//        GravityStrengthMobEffect.init();
//        GravityPotions.init();

        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> CommonEvents.handleGravity(entity,(IEntityGravityData)component));

    }

}
