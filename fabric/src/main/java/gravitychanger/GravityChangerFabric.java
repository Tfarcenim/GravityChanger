package gravitychanger;

import gravitychanger.api.IEntityGravityData;
import gravitychanger.api.RotationParameters;
import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.GravityCommand;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.config.GravityChangerConfig;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.item.GravityChangerItem;
import gravitychanger.item.GravityChangerItemAOE;
import gravitychanger.mob_effect.refined.GravityStrengthStatusEffect;
import gravitychanger.plating.GravityPlatingBlockEntity;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        GravityChanger.register();

        GravityChangerItem.init();
        GravityChangerItemAOE.init();
        GravityChanger.LOG.info("[gravity-changer]");

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );

        GravityDirectionStatusEffect.init();
        GravityStrengthStatusEffect.init();
        GravityInvertStatusEffect.init();
        gravitychanger.mob_effect.refined.GravityPotions.init();

//        GravityDirectionMobEffect.registerEffects();
//        GravityDirectionMobEffect.registerEvent();
//        GravityInvertMobEffect.init();
//        GravityStrengthMobEffect.init();
//        GravityPotions.init();
        
        //GravityPlatingBlock.init();
        //GravityPlatingItem.init();
        GravityPlatingBlockEntity.init();
        
        DirectionArgumentType.init();
        LocalDirectionArgumentType.init();

        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> CommonEvents.handleGravity(entity,(IEntityGravityData)component));

    }

}
