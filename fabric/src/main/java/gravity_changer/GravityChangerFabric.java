package gravity_changer;

import gravity_changer.command.ArgumentTypes;
import gravity_changer.command.DirectionArgumentType;
import gravity_changer.command.GravityCommand;
import gravity_changer.command.LocalDirectionArgumentType;
import gravity_changer.item.GravityAnchorItem;
import gravity_changer.mob_effect.GravityPotion;
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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class GravityChangerFabric implements ModInitializer {

    public static CreativeModeTab GravityChangerGroup;

    @Override
    public void onInitialize() {
        GravityChangerItem.init();
        GravityChangerItemAOE.init();

        GravityChanger.init();

        for (Direction direction : Direction.values()) {
            Registry.register(
                    BuiltInRegistries.ITEM, GravityAnchorItem.getItemId(direction), GravityAnchorItem.ITEM_MAP.get(direction)
            );
        }

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
        
        GravityChangerGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_DOWN));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_EAST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_WEST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_NORTH));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_SOUTH));
                
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_UP_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_DOWN_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_EAST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_WEST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_NORTH_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_SOUTH_AOE));
                
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 1)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 2)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 8)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 32)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 64)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(false, 8)
                ));
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(false, 32)
                ));
                
                for (GravityAnchorItem item : GravityAnchorItem.ITEM_MAP.values()) {
                    entries.accept(new ItemStack(item));
                }
                
                // gravity potions are both in food tab and gravity changer tab
                Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
                
                for (Item potionItem : potionItems) {
                    for (Potion potion : GravityPotion.ALL) {
                        ItemStack stack = PotionUtils.setPotion(new ItemStack(potionItem), potion);
                        entries.accept(stack);
                    }
                }
            })
            .title(Component.translatable("itemGroup.gravity_changer.general"))
            .build();
        
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, GravityChanger.id("general"),
            GravityChangerGroup
        );
        
        GravityDirectionMobEffect.init();
        GravityInvertMobEffect.init();
        GravityStrengthMobEffect.init();
        GravityPotion.init();
        
        GravityPlatingBlock.init();
        GravityPlatingItem.init();
        GravityPlatingBlockEntity.init();
        
        registerArgumentTYpes();
        registerArgumentTYpes();
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
