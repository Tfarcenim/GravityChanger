package gravity_changer.item;

import gravity_changer.GravityChangerMod;
import gravity_changer.api.GravityChangerAPI;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GravityChangerItemAOE extends Item {
    public static final Item GRAVITY_CHANGER_DOWN_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST_AOE = new GravityChangerItemAOE(new Settings().maxCount(1), Direction.EAST);
    
    public final Direction gravityDirection;
    
    public GravityChangerItemAOE(Settings settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            Box box = user.getBoundingBox().expand(3);
            List<Entity> list = world.getEntitiesByClass(Entity.class, box, e -> !(e instanceof PlayerEntity));
            for (Entity entity : list) {
                GravityChangerAPI.setBaseGravityDirection(entity, gravityDirection);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    
    public static void init() {
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_down_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_DOWN_AOE);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_up_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_UP_AOE);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_north_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_NORTH_AOE);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_south_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_SOUTH_AOE);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_west_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_WEST_AOE);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_east_aoe"), GravityChangerItemAOE.GRAVITY_CHANGER_EAST_AOE);
    }
}
