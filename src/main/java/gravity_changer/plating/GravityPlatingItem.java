package gravity_changer.plating;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GravityPlatingItem extends BlockItem {
    public static final Item PLATING_BLOCK_ITEM = new GravityPlatingItem(GravityPlatingBlock.PLATING_BLOCK, new Settings());
    
    public static void init() {
        Registry.register(
            Registries.ITEM, Identifier.of("gravity_changer:plating"),
            GravityPlatingItem.PLATING_BLOCK_ITEM
        );
    }

    public GravityPlatingItem(Block block, Settings properties) {
        super(block, properties);
    }
    
    public static @Nullable GravityPlatingBlockEntity.SideData getSideData(@Nullable NbtCompound tag) {
        if (tag == null) {
            return null;
        }
        
        if (tag.contains("sideData")) {
            NbtCompound t = tag.getCompound("sideData");
            return GravityPlatingBlockEntity.SideData.fromTag(t);
        }
        return null;
    }
    
    public static void setSideData(NbtCompound tag, @Nullable GravityPlatingBlockEntity.SideData sideData) {
        if (sideData != null) {
            tag.put("sideData", sideData.toTag());
        }
        else {
            tag.remove("sideData");
        }
    }
    
    public static ItemStack createStack(@Nullable GravityPlatingBlockEntity.SideData sideData) {
        ItemStack itemStack = new ItemStack(GravityPlatingItem.PLATING_BLOCK_ITEM);
        //setSideData(itemStack.getOrCreateNbt(), sideData);
        return itemStack;
    }
    
    @Override
    public Text getName(ItemStack stack) {
        GravityPlatingBlockEntity.SideData sideData = null;//getSideData(stack.getNbt());
        if (sideData != null) {
            return Text.translatable(
                "gravity_changer.plating.item_name",
                sideData.level, GravityPlatingBlockEntity.getForceText(sideData.isAttracting)
            );
        }
        
        return super.getName(stack);
    }
    
    @Override
    public ActionResult place(ItemPlacementContext context) {
        ActionResult result = super.place(context);
        
        World level = context.getWorld();
        ItemStack itemStack = context.getStack();
        BlockPos clickedPos = context.getBlockPos();
        
        if (level.isClient()) {
            return result;
        }
        
        GravityPlatingBlockEntity.SideData sideData = null;//getSideData(itemStack.getOrCreateNbt());
        
        if (sideData != null) {
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            if (blockEntity instanceof GravityPlatingBlockEntity be) {
                be.onPlacing(context.getSide().getOpposite(), sideData);
            }
        }
        
        return result;
    }
    
    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("gravity_changer.plating.tooltip.0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("gravity_changer.plating.tooltip.1").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("gravity_changer.plating.tooltip.2").formatted(Formatting.GRAY));
    }
}