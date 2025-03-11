package gravitychanger.init;

import gravitychanger.item.GravityAnchorItem;
import gravitychanger.mob_effect.GravityPotions;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class ModCreativeTabs {
    public static final CreativeModeTab GENERAL = CreativeModeTab.builder(null,-1)
            .icon(() -> new ItemStack(ModItems.GRAVITY_CHANGER_UP))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_UP));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_DOWN));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_EAST));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_WEST));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_NORTH));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_SOUTH));

                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_UP_AOE));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_DOWN_AOE));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_EAST_AOE));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_WEST_AOE));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_NORTH_AOE));
                entries.accept(new ItemStack(ModItems.GRAVITY_CHANGER_SOUTH_AOE));

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
                    for (Potion potion : GravityPotions.ALL) {
                        ItemStack stack = PotionUtils.setPotion(new ItemStack(potionItem), potion);
                        entries.accept(stack);
                    }
                }
            })
            .title(Component.translatable("itemGroup.gravitychanger.general"))
            .build();
}
