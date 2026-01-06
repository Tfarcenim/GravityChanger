package gravity_changer.plating;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Based on code from AmethystGravity (by CyborgCabbage)
 */
public class GravityPlatingBlock extends BlockWithEntity {
    public static final MapCodec<GravityPlatingBlock> CODEC = createCodec(GravityPlatingBlock::new);
    
    // in a corner, multiple faces of plates can occupy the same block
    
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesByState;

    public static final Block PLATING_BLOCK = new GravityPlatingBlock(
        Settings.create().nonOpaque().noCollision().breakInstantly()
    );
    
    public static void init() {
        Registry.register(
            Registries.BLOCK, Identifier.of("gravity_changer:plating"), PLATING_BLOCK
        );
    }
    
    public GravityPlatingBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(NORTH, false)
            .with(EAST, false)
            .with(SOUTH, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false)
        );
        this.shapesByState =
            ImmutableMap.copyOf(
                this.stateManager.getStates().stream()
                    .collect(Collectors.toMap(Function.identity(), GravityPlatingBlock::getShapeForState))
            );
    }
    
    @Override
    protected @NotNull MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (state.get(UP)) {
            voxelShape = UP_SHAPE;
        }
        if (state.get(NORTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        }
        if (state.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        }
        if (state.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        }
        if (state.get(WEST)) {
            voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        }
        if (state.get(DOWN)) {
            voxelShape = VoxelShapes.union(voxelShape, DOWN_SHAPE);
        }
        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapesByState.get(state);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }
    
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (hasDir(state, direction) && !canPlaceOn(world, pos.offset(direction), direction.getOpposite())) {
            state = state.with(directionToProperty(direction), false);
            if (getDirections(state).size() == 0) {
                return Blocks.AIR.getDefaultState();
            }
            else {
                return state;
            }
        }
        else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }
    
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return (((state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return (((state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90 -> {
                return (((state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }
    
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return (state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK -> {
                return (state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GravityPlatingBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient)
            return validateTicker(type, GravityPlatingBlockEntity.TYPE, GravityPlatingBlockEntity::tick);
        else
            return validateTicker(type, GravityPlatingBlockEntity.TYPE, GravityPlatingBlockEntity::tick);
    }
    
    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem()) {
            return !hasDir(state, context.getSide().getOpposite());
        }
        return super.canReplace(state, context);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return blockState.with(directionToProperty(ctx.getSide().getOpposite()), true);
        }
        return getDefaultState().with(directionToProperty(ctx.getSide().getOpposite()), true);
    }
    
    private boolean canPlaceOn(BlockView world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isSideSolidFullSquare(world, pos, side);
    }
    
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        ArrayList<Direction> directions = getDirections(state);
        if (directions.size() == 1) {
            return canPlaceOn(world, pos.offset(directions.get(0)), directions.get(0).getOpposite());
        }
        //Placing inside an existing plating
        if (directions.size() > 1) {
            for (Direction dir : getDirections(world.getBlockState(pos))) {
                directions.remove(dir);
            }
            return canPlaceOn(world, pos.offset(directions.get(0)), directions.get(0).getOpposite());
        }
        return false;
    }
    
    public static BooleanProperty directionToProperty(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
    
    // Note: the direction is gravity field direction. the facing is the opposite
    public static boolean hasDir(BlockState blockState, Direction dir) {
        return blockState.get(directionToProperty(dir));
    }
    
    public static ArrayList<Direction> getDirections(BlockState blockState) {
        ArrayList<Direction> list = new ArrayList<>();
        //Iterate directions
        for (int directionId = 0; directionId < 6; directionId++) {
            //Convert ID to Direction
            Direction direction = Direction.byId(directionId);
            //If the plate has this direction
            if (hasDir(blockState, direction)) {
                list.add(direction);
            }
        }
        return list;
    }
    
    @Override
    public ActionResult onUse(
        BlockState state, World level, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (level.isClient()) {
            return ActionResult.SUCCESS;
        }
        
        Direction hitDir = hit.getSide();
        Direction plateDir = hitDir.getOpposite();
        
        BlockEntity blockEntity = level.getBlockEntity(pos);
        
        if (!(blockEntity instanceof GravityPlatingBlockEntity be)) {
            return ActionResult.FAIL;
        }
        
        return be.interact(level, pos, plateDir, player);
    }
    
    /**
     * Similar to {@link ShulkerBoxBlock#onBreak(World, BlockPos, BlockState, PlayerEntity)}
     * Make it drop in creative mode.
     *
     * @return
     */
    /*@Override
    public BlockState onBreak(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GravityPlatingBlockEntity be &&
            !level.isClient && player.isCreative()
        ) {
            List<ItemStack> drops = be.getDrops();
        
            for (ItemStack itemStack : drops) {
                ItemEntity itemEntity = new ItemEntity(
                    level,
                    (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5,
                    itemStack
                );
                itemEntity.setToDefaultPickupDelay();
                level.spawnEntity(itemEntity);
            }
        }
        
        return super.onBreak(level, pos, state, player);
    }*/
    
    /*@Override
    public List<ItemStack> getDroppedStacks(BlockState blockState, LootContextParameterSet.Builder builder) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof GravityPlatingBlockEntity be) {
            return be.getDrops();
        }
        
        return List.of();
    }*/
}
