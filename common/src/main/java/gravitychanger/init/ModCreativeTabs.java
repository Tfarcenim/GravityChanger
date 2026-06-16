package gravitychanger.init;

import gravitychanger.GravityChanger;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.item.GravityChangerItem;
import gravitychanger.item.GravityChangerItemAOE;
import gravitychanger.mob_effect.refined.GravityPotions;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class ModCreativeTabs {
    public static final CreativeModeTab GENERAL = CreativeModeTab.builder(null,-1)
            .icon(() -> new ItemStack(ModItems.GRAVITY_CHANGERS.getEntry(Direction.UP)))
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

                for (GravityAnchorItem item : ModItems.GRAVITY_ANCHORS.map().values()) {
                    entries.accept(new ItemStack(item));
                }

                for (GravityChangerItem item : ModItems.GRAVITY_CHANGERS.map().values()) {
                    entries.accept(new ItemStack(item));
                }

                for (GravityChangerItemAOE item : ModItems.GRAVITY_CHANGERS_AOE.map().values()) {
                    entries.accept(new ItemStack(item));
                }

                // gravity potions are both in food tab and gravity changer tab
                Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

                for (Item potionItem : potionItems) {
                    for (Potion potion : GravityPotions.ALL) {
                        Holder<Potion> holder = new Holder.Direct<>(potion);
                        ItemStack stack = PotionContents.createItemStack(potionItem, holder);
                        entries.accept(stack);
                    }
                }
            })
            .title(Component.translatable("itemGroup.gravitychanger.general"))
            .build();

    static {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, GravityChanger.id("general"), ModCreativeTabs.GENERAL);
    }

    public static void init() {
    }
}
