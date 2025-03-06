package gravity_changer.init;

import gravity_changer.item.GravityAnchorItem;
import gravity_changer.item.GravityChangerItem;
import gravity_changer.item.GravityChangerItemAOE;
import gravity_changer.mob_effect.GravityPotions;
import gravity_changer.plating.GravityPlatingBlockEntity;
import gravity_changer.plating.GravityPlatingItem;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class ModCreativeModeTab {
    public static CreativeModeTab GravityChangerGroup = CreativeModeTab.builder(null, -1)
            .icon(() -> new ItemStack(GravityChangerItem.ITEM_MAP.get(Direction.UP)))
            .displayItems((enabledFeatures, entries) -> {

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

                for (GravityChangerItem item : GravityChangerItem.ITEM_MAP.values()) {
                    entries.accept(new ItemStack(item));
                }

                for (GravityChangerItemAOE item : GravityChangerItemAOE.ITEM_MAP.values()) {
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
