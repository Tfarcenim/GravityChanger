package gravitychanger.init;

import gravitychanger.DirectionFamily;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.item.GravityChangerItem;
import gravitychanger.item.GravityChangerItemAOE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final DirectionFamily<GravityAnchorItem> GRAVITY_ANCHORS = DirectionFamily.createAndRegister(
            BuiltInRegistries.ITEM,direction -> new  GravityAnchorItem(direction, new Item.Properties()),
                    "gravity_anchor","");

    public static final DirectionFamily<GravityChangerItem> GRAVITY_CHANGERS = DirectionFamily.createAndRegister(
            BuiltInRegistries.ITEM,direction -> new GravityChangerItem(new Item.Properties().stacksTo(1), direction),
            "gravity_changer","");

    public static final DirectionFamily<GravityChangerItemAOE> GRAVITY_CHANGERS_AOE = DirectionFamily.createAndRegister(
            BuiltInRegistries.ITEM,direction -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), direction),
            "gravity_changer","_aoe");

    static {

    }
    
    public static void init() {
        
    }

}
