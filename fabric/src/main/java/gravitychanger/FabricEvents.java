package gravitychanger;

import gravitychanger.init.ModItems;
import gravitychanger.item.GravityAnchorItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FabricEvents {
    public static void init() {
        for (Direction direction : Direction.values()) {
            Registry.register(
                BuiltInRegistries.ITEM, GravityAnchorItem.getItemId(direction), GravityAnchorItem.ITEM_MAP.get(direction)
            );
        }

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_down_aoe"), ModItems.GRAVITY_CHANGER_DOWN_AOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_up_aoe"), ModItems.GRAVITY_CHANGER_UP_AOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_north_aoe"), ModItems.GRAVITY_CHANGER_NORTH_AOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_south_aoe"), ModItems.GRAVITY_CHANGER_SOUTH_AOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_west_aoe"), ModItems.GRAVITY_CHANGER_WEST_AOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityChanger.MOD_ID, "gravity_changer_east_aoe"), ModItems.GRAVITY_CHANGER_EAST_AOE);

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
    }
}
