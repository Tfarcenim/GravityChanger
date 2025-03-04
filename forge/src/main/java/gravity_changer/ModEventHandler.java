package gravity_changer;

import gravity_changer.api.GravityUpdateEvent;
import gravity_changer.item.GravityAnchorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModEventHandler {

    //        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
    //            for (ItemStack handSlot : entity.getHandSlots()) {
    //                Item item = handSlot.getItem();
    //                if (item instanceof GravityAnchorItem anchorItem) {
    //                    component.applyGravityDirectionEffect(
    //                            anchorItem.direction,
    //                            null, 1000000
    //                    );
    //                }
    //            }
    //        });

    static void init() {
        MinecraftForge.EVENT_BUS.addListener(ModEventHandler::updateGravityAnchor);
    }

    static void updateGravityAnchor(GravityUpdateEvent event) {
        Entity entity = event.getEntity();
        for (ItemStack handSlot : entity.getHandSlots()) {
            Item item = handSlot.getItem();
            if (item instanceof GravityAnchorItem anchorItem) {
                event.getGravity().applyGravityDirectionEffect(
                        anchorItem.direction,
                        null, 1000000
                );
            }
        }
    }
}
