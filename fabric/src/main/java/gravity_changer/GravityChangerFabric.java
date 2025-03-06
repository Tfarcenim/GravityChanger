package gravity_changer;

import gravity_changer.command.ArgumentTypes;
import gravity_changer.command.DirectionArgumentType;
import gravity_changer.command.GravityCommand;
import gravity_changer.command.LocalDirectionArgumentType;
import gravity_changer.init.ModCreativeModeTab;
import gravity_changer.item.GravityAnchorItem;
import gravity_changer.mob_effect.GravityPotions;
import gravity_changer.mob_effect.GravityStrengthMobEffect;
import gravity_changer.plating.GravityPlatingBlock;
import gravity_changer.plating.GravityPlatingBlockEntity;
import gravity_changer.item.GravityChangerItem;
import gravity_changer.item.GravityChangerItemAOE;
import gravity_changer.mob_effect.GravityDirectionMobEffect;
import gravity_changer.mob_effect.GravityInvertMobEffect;
import gravity_changer.plating.GravityPlatingItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class GravityChangerFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        GravityChanger.init();



        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            for (ItemStack handSlot : entity.getHandSlots()) {
                Item item = handSlot.getItem();
                if (item instanceof GravityAnchorItem anchorItem) {
                    component.applyGravityDirectionEffect(
                            anchorItem.direction,
                            null, 1000000
                    );
                }
            }
        });

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );

        registerObjects();

        registerArgumentTYpes();
    }

    void events() {

    }

    void registerObjects() {

        for (Direction dir : Direction.values()) {
            Registry.register(
                    BuiltInRegistries.MOB_EFFECT, GravityChanger.id(dir.toString()), GravityDirectionMobEffect.EFFECT_MAP.get(dir)
            );
        }

        GravityComponent.GRAVITY_UPDATE_EVENT.register(
                GravityDirectionMobEffect.PHASE, (entity, component) -> {
                    if (!(entity instanceof LivingEntity livingEntity)) {
                        return;
                    }

                    for (GravityDirectionMobEffect dirEffect : GravityDirectionMobEffect.EFFECT_MAP.values()) {
                        MobEffectInstance effectInstance = livingEntity.getEffect(dirEffect);
                        if (effectInstance != null) {
                            int amplifier = effectInstance.getAmplifier();

                            component.applyGravityDirectionEffect(
                                    dirEffect.gravityDirection,
                                    null,
                                    amplifier + 1.0
                            );
                        }
                    }
                }
        );


        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB, GravityChanger.id("general"),
                ModCreativeModeTab.GravityChangerGroup
        );

        for (Direction direction : Direction.values()) {
            Registry.register(
                    BuiltInRegistries.ITEM,GravityChanger.id("gravity_anchor_"+direction), GravityAnchorItem.ITEM_MAP.get(direction)
            );
            Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_"+direction), GravityChangerItem.ITEM_MAP.get(direction));
            Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_"+direction+"_aoe"), GravityChangerItemAOE.ITEM_MAP.get(direction));

        }

        Registry.register(
                BuiltInRegistries.POTION,
                GravityChanger.id("gravity_decr_0"),
                GravityPotions.STRENGTH_DECR_POTION_0
        );

        Registry.register(
                BuiltInRegistries.POTION,
                GravityChanger.id("gravity_decr_1"),
                GravityPotions.STRENGTH_DECR_POTION_1
        );

        Registry.register(
                BuiltInRegistries.POTION,
                GravityChanger.id("gravity_incr_0"),
                GravityPotions.STRENGTH_INCR_POTION_0
        );

        Registry.register(
                BuiltInRegistries.POTION,
                GravityChanger.id("gravity_incr_1"),
                GravityPotions.STRENGTH_INCR_POTION_1
        );

        for (Direction direction : Direction.values()) {
            Potion potion = GravityPotions.DIR_POTIONS.get(direction);
            Registry.register(
                    BuiltInRegistries.POTION,
                    GravityPotions.getPotionId(direction),
                    potion
            );
        }

        Registry.register(
                BuiltInRegistries.BLOCK, GravityChanger.id("gravity_plating"), GravityPlatingBlock.PLATING_BLOCK
        );

        Registry.register(
                BuiltInRegistries.ITEM, GravityChanger.id("gravity_plating"),
                GravityPlatingItem.PLATING_BLOCK_ITEM
        );

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GravityChanger.id("gravity_plating"), GravityPlatingBlockEntity.TYPE);

        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity livingEntity) {
                GravityStrengthMobEffect.INCREASE.apply(livingEntity);
                GravityStrengthMobEffect.DECREASE.apply(livingEntity);
                GravityStrengthMobEffect.REVERSE.apply(livingEntity);
            }
        });

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_increase"),
                GravityStrengthMobEffect.INCREASE
        );

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_decrease"),
                GravityStrengthMobEffect.DECREASE
        );

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_reverse"),
                GravityStrengthMobEffect.REVERSE
        );

        GravityComponent.GRAVITY_UPDATE_EVENT.register(
                GravityInvertMobEffect.PHASE, (entity, component) -> {
                    if (entity instanceof LivingEntity livingEntity) {
                        if (livingEntity.hasEffect(GravityInvertMobEffect.INSTANCE)) {
                            component.applyGravityDirectionEffect(
                                    component.getCurrGravityDirection().getOpposite(),
                                    null, 5
                            );
                        }
                    }
                }
        );

        // apply invert after gravity effect
        GravityComponent.GRAVITY_UPDATE_EVENT.addPhaseOrdering(
                GravityDirectionMobEffect.PHASE, GravityInvertMobEffect.PHASE
        );

        Registry.register(
                BuiltInRegistries.MOB_EFFECT, GravityChanger.id("invert"), GravityInvertMobEffect.INSTANCE
        );

    }

    public static void registerArgumentTYpes() {
        ArgumentTypeRegistry.registerArgumentType(
                GravityChanger.id("direction"),
                DirectionArgumentType.class,
                SingletonArgumentInfo.contextFree(() -> ArgumentTypes.DIRECTION)
        );
        ArgumentTypeRegistry.registerArgumentType(
                GravityChanger.id("local_direction"),
                LocalDirectionArgumentType.class,
                SingletonArgumentInfo.contextFree(() -> ArgumentTypes.LOCAL_DIRECTION)
        );
    }



}
