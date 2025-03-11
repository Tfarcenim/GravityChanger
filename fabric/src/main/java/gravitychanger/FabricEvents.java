package gravitychanger;

import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.init.ModItems;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import gravitychanger.mob_effect.GravityPotions;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import static gravitychanger.mob_effect.GravityStrengthMobEffect.*;

public class FabricEvents {
    public static void init() {

        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity livingEntity) {
                INCREASE.apply(livingEntity, component);
                DECREASE.apply(livingEntity, component);
                REVERSE.apply(livingEntity, component);
            }
        });

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_increase"),
                INCREASE
        );

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_decrease"),
                DECREASE
        );

        Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GravityChanger.id("strength_reverse"),
                REVERSE
        );


        for (Direction direction : Direction.values()) {
            Registry.register(
                BuiltInRegistries.ITEM,  GravityChanger.id( "gravity_anchor_" + direction.getName()), GravityAnchorItem.ITEM_MAP.get(direction)
            );
        }

        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_down_aoe"), ModItems.GRAVITY_CHANGER_DOWN_AOE);
        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_up_aoe"), ModItems.GRAVITY_CHANGER_UP_AOE);
        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_north_aoe"), ModItems.GRAVITY_CHANGER_NORTH_AOE);
        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_south_aoe"), ModItems.GRAVITY_CHANGER_SOUTH_AOE);
        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_west_aoe"), ModItems.GRAVITY_CHANGER_WEST_AOE);
        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_changer_east_aoe"), ModItems.GRAVITY_CHANGER_EAST_AOE);

        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_down"), ModItems.GRAVITY_CHANGER_DOWN);
        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_up"), ModItems.GRAVITY_CHANGER_UP);
        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_north"), ModItems.GRAVITY_CHANGER_NORTH);
        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_south"), ModItems.GRAVITY_CHANGER_SOUTH);
        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_west"), ModItems.GRAVITY_CHANGER_WEST);
        Registry.register(BuiltInRegistries.ITEM,GravityChanger.id("gravity_changer_east"), ModItems.GRAVITY_CHANGER_EAST);

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

        for (Direction dir : Direction.values()) {
            Registry.register(
                    BuiltInRegistries.MOB_EFFECT, GravityChanger.id(dir+""), GravityDirectionMobEffect.EFFECT_MAP.get(dir)
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
                BuiltInRegistries.MOB_EFFECT,GravityChanger.id("invert"), GravityInvertMobEffect.INSTANCE
        );

        initArgs();
    }

    public static void initArgs() {
        ArgumentTypeRegistry.registerArgumentType(
                GravityChanger.id("local_direction"),
                LocalDirectionArgumentType.class,
                SingletonArgumentInfo.contextFree(() -> LocalDirectionArgumentType.instance)
        );

        ArgumentTypeRegistry.registerArgumentType(
                GravityChanger.id("direction"),
                DirectionArgumentType.class,
                SingletonArgumentInfo.contextFree(() -> DirectionArgumentType.instance)
        );
    }

}
