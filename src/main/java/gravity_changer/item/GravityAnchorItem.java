package gravity_changer.item;

import gravity_changer.GravityComponent;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

// based on AmethystGravity
public class GravityAnchorItem extends Item {
    public final Direction direction;
    
    public static final EnumMap<Direction, GravityAnchorItem> ITEM_MAP = new EnumMap<>(Direction.class);
    
    static {
        for (Direction direction : Direction.values()) {
            ITEM_MAP.put(direction, new GravityAnchorItem(direction, new Settings()));
        }
    }
    
    public static void init() {
        for (Direction direction : Direction.values()) {
            Registry.register(
                Registries.ITEM, getItemId(direction), ITEM_MAP.get(direction)
            );
        }

        //TODO: Verify that this does not create problems,
        // this use of the event should only apply to things with hands
        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if(entity instanceof LivingEntity) {
                for (ItemStack handSlot : ((LivingEntity) entity).getHandItems()) {
                    Item item = handSlot.getItem();
                    if (item instanceof GravityAnchorItem anchorItem) {
                        component.applyGravityDirectionEffect(
                                anchorItem.direction,
                                null, 1000000
                        );
                    }
                }
            }
        });
    }
    
    public static Identifier getItemId(Direction direction) {
        return Identifier.of("gravity_changer", "gravity_anchor_" + direction.getName());
    }
    
    public GravityAnchorItem(Direction _direction, Settings settings) {
        super(settings);
        direction = _direction;
    }
    
    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(
            Text.translatable("gravity_changer.gravity_anchor.tooltip.0")
                .formatted(Formatting.GRAY)
        );
        
        tooltip.add(
            Text.translatable("gravity_changer.gravity_anchor.tooltip.1")
                .formatted(Formatting.GRAY)
        );
    }
}
