package gravity_changer.item;

import gravity_changer.GravityChangerMod;
import gravity_changer.api.GravityChangerAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class GravityChangerItem extends Item {
    public static final Item GRAVITY_CHANGER_DOWN = new GravityChangerItem(new Settings().maxCount(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP = new GravityChangerItem(new Settings().maxCount(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH = new GravityChangerItem(new Settings().maxCount(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH = new GravityChangerItem(new Settings().maxCount(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST = new GravityChangerItem(new Settings().maxCount(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST = new GravityChangerItem(new Settings().maxCount(1), Direction.EAST);
    
    public final Direction gravityDirection;
    
    public GravityChangerItem(Settings settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient())
            GravityChangerAPI.setBaseGravityDirection(user, gravityDirection);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(
            Text.translatable("gravity_changer.gravity_changer.tooltip.0")
                .formatted(Formatting.GRAY)
        );
        tooltip.add(
            Text.translatable("gravity_changer.gravity_changer.tooltip.1")
                .formatted(Formatting.GRAY)
        );
    }
    
    public static void init() {
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_down"), GRAVITY_CHANGER_DOWN);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_up"), GRAVITY_CHANGER_UP);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_north"), GRAVITY_CHANGER_NORTH);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_south"), GRAVITY_CHANGER_SOUTH);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_west"), GRAVITY_CHANGER_WEST);
        Registry.register(Registries.ITEM, Identifier.of(GravityChangerMod.NAMESPACE, "gravity_changer_east"), GRAVITY_CHANGER_EAST);
    }
    
}
