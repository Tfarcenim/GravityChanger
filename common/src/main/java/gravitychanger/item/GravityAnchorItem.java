package gravitychanger.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

// based on AmethystGravity
public class GravityAnchorItem extends Item {
    public final Direction direction;

    public GravityAnchorItem(Direction _direction, Properties settings) {
        super(settings);
        direction = _direction;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(
                Component.translatable("gravity_changer.gravity_anchor.tooltip.0")
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable("gravity_changer.gravity_anchor.tooltip.1")
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}
