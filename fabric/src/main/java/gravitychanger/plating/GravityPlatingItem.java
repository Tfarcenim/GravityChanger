package gravitychanger.plating;

import gravitychanger.GravityChangerMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GravityPlatingItem extends BlockItem {

    public static final DataComponentType<CustomData> SIDE_DATA_COMPONENT = DataComponentType.<CustomData>builder()
            .persistent(CustomData.CODEC)
            .networkSynchronized(CustomData.STREAM_CODEC)
            .build();

    public static final Item PLATING_BLOCK_ITEM = new GravityPlatingItem(GravityPlatingBlock.PLATING_BLOCK, new Properties());
    
    public static void init() {

        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "side_data"),
                SIDE_DATA_COMPONENT
        );

        Registry.register(
            BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "plating"),
            GravityPlatingItem.PLATING_BLOCK_ITEM
        );
    }
    
    public GravityPlatingItem(Block block, Properties properties) {
        super(block, properties);
    }
    
//    public static @Nullable GravityPlatingBlockEntity.SideData getSideData(@Nullable CompoundTag tag) {
//        if (tag == null) {
//            return null;
//        }
//
//        if (tag.contains("sideData")) {
//            CompoundTag t = tag.getCompound("sideData");
//            return GravityPlatingBlockEntity.SideData.fromTag(t);
//        }
//        return null;
//    }

    public static @Nullable GravityPlatingBlockEntity.SideData getSideData(ItemStack stack) {

        CustomData customData = stack.get(SIDE_DATA_COMPONENT);
        if (customData == null) return null;

        customData.update(tag ->
                GravityPlatingBlockEntity.SideData.fromTag(tag.getCompound("sideData"))
        );
        CompoundTag t = customData.copyTag().getCompound("sideData");
        return GravityPlatingBlockEntity.SideData.fromTag(t);
    }
    
//    public static void setSideData(@Nullable CompoundTag tag, @Nullable GravityPlatingBlockEntity.SideData sideData) {
//        if (tag == null) {
//            CompoundTag tag1 = new CompoundTag();
//            if (sideData != null) {
//                tag1.put("sideData", sideData.toTag());
//            }
//            else {
//                tag1.remove("sideData");
//            }
//            return;
//        }
//        if (sideData != null) {
//            tag.put("sideData", sideData.toTag());
//        }
//        else {
//            tag.remove("sideData");
//        }
//    }

    public static void setSideData(ItemStack stack, @Nullable GravityPlatingBlockEntity.SideData sideData) {
        if (sideData != null) {

            CustomData.update(SIDE_DATA_COMPONENT, stack, tag -> {
                CompoundTag sideTag = new CompoundTag();
                sideData.toTag();
                tag.put("sideData", sideTag);
                CustomData data = CustomData.of(tag);
                stack.set(SIDE_DATA_COMPONENT, data);
            });



        } else {

            stack.remove(SIDE_DATA_COMPONENT);
        }
    }
    
    public static ItemStack createStack(@Nullable GravityPlatingBlockEntity.SideData sideData) {
        ItemStack itemStack = new ItemStack(GravityPlatingItem.PLATING_BLOCK_ITEM);
        setSideData(itemStack, sideData);
        return itemStack;
    }
    
    @Override
    public @NotNull Component getName(ItemStack stack) {
        GravityPlatingBlockEntity.SideData sideData = getSideData(stack);
        if (sideData != null) {
            return Component.translatable(
                "gravity_changer.plating.item_name",
                sideData.level, GravityPlatingBlockEntity.getForceText(sideData.isAttracting)
            );
        }
        
        return super.getName(stack);
    }
    
    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        
        Level level = context.getLevel();
        ItemStack itemStack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        
        if (level.isClientSide()) {
            return result;
        }
        
        GravityPlatingBlockEntity.SideData sideData = getSideData(itemStack);
        
        if (sideData != null) {
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            if (blockEntity instanceof GravityPlatingBlockEntity be) {
                be.onPlacing(context.getClickedFace().getOpposite(), sideData);
            }
        }
        
        return result;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        list.add(Component.translatable("gravity_changer.plating.tooltip.0").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("gravity_changer.plating.tooltip.1").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("gravity_changer.plating.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}