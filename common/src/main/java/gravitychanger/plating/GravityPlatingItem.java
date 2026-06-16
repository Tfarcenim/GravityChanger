package gravitychanger.plating;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GravityPlatingItem extends BlockItem {
    public static final Item PLATING_BLOCK_ITEM = new GravityPlatingItem(GravityPlatingBlock.PLATING_BLOCK, new Properties());

    public static final DataComponentType<GravityPlatingBlockEntity.SideData> SIDE_DATA_COMPONENT = DataComponentType
            .<GravityPlatingBlockEntity.SideData>builder()
            .persistent(GravityPlatingBlockEntity.SideData.CODEC)
            .networkSynchronized(GravityPlatingBlockEntity.SideData.STREAM_CODEC)
            .build();

    public GravityPlatingItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack createStack(@Nullable GravityPlatingBlockEntity.SideData sideData) {
        ItemStack itemStack = new ItemStack(GravityPlatingItem.PLATING_BLOCK_ITEM);
        itemStack.set(SIDE_DATA_COMPONENT, sideData);
        return itemStack;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        GravityPlatingBlockEntity.SideData sideData = stack.get(SIDE_DATA_COMPONENT);
        if (sideData != null) {
            return Component.translatable(
                "gravitychanger.plating.item_name",
                sideData.level, GravityPlatingBlockEntity.getForceText(sideData.isAttracting)
            );
        }
        
        return super.getName(stack);
    }
    
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        
        Level level = context.getLevel();
        ItemStack itemStack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        
        if (level.isClientSide()) {
            return result;
        }
        
        GravityPlatingBlockEntity.SideData sideData = itemStack.get(SIDE_DATA_COMPONENT);
        
        if (sideData != null) {
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            if (blockEntity instanceof GravityPlatingBlockEntity be) {
                be.onPlacing(context.getClickedFace().getOpposite(), sideData);
            }
        }
        
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("gravitychanger.plating.tooltip.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravitychanger.plating.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravitychanger.plating.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}