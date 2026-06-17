package gravitychanger;

import gravitychanger.api.GravityChangerAPIFabric;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.command.GravityCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class GravityChangerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GravityChanger.init();
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

        GravityChangerAPIFabric.GRAVITY_UPDATE_EVENT.register(CommonEvents::handleGravity);

    }

}
