package gravity_changer.plating;

import com.mojang.logging.LogUtils;
import gravity_changer.EntityTags;
import gravity_changer.GravityChangerMod;
import gravity_changer.GravityComponent;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.GCUtil;
import gravity_changer.util.RotationUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on code from AmethystGravity (by CyborgCabbage)
 */

public class GravityPlatingBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final Identifier ID = Identifier.of("gravity_changer:plating_block_entity");
    public static BlockEntityType<GravityPlatingBlockEntity> TYPE;
    
    private static final int MAX_LEVEL = 64;
    
    public static void init() {

        TYPE = BlockEntityType.Builder.create(
            GravityPlatingBlockEntity::new, GravityPlatingBlock.PLATING_BLOCK
        ).build();
        Registry.register(Registries.BLOCK_ENTITY_TYPE, ID, TYPE);
    }
    
    public GravityPlatingBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    public static class SideData {
        public boolean isAttracting = true;
        public int level = 1;
        
        public @Nullable Box effectBoxCache = null;
        
        public SideData(boolean isAttracting, int level) {
            this.isAttracting = isAttracting;
            this.level = level;
        }
        
        public static SideData createDefault() {
            return new SideData(true, 1);
        }
        
        public static SideData fromTag(NbtCompound tag) {
            boolean isAttracting_ = tag.getBoolean("isAttracting");
            int level_ = tag.getInt("world");
            
            level_ = MathHelper.clamp(level_, 1, MAX_LEVEL);
            
            return new SideData(isAttracting_, level_);
        }
        
        public NbtCompound toTag() {
            NbtCompound tag = new NbtCompound();
            tag.putBoolean("isAttracting", isAttracting);
            tag.putInt("world", level);
            return tag;
        }
        
        public double getEffectRange() {
            return level - 0.1;
        }
        
        public Box getEffectBox(BlockPos blockPos, Direction plateDir, World world) {
            if (effectBoxCache == null) {
                double expand = 0.001;
                
                double minX = blockPos.getX() - expand;
                double minY = blockPos.getY() - expand;
                double minZ = blockPos.getZ() - expand;
                double maxX = blockPos.getX() + 1 + expand;
                double maxY = blockPos.getY() + 1 + expand;
                double maxZ = blockPos.getZ() + 1 + expand;
                
                double delta = getEffectRange() - 1;
                switch (plateDir) {
                    case DOWN -> maxY += delta;
                    case UP -> minY -= delta;
                    case NORTH -> maxZ += delta;
                    case SOUTH -> minZ -= delta;
                    case WEST -> maxX += delta;
                    case EAST -> minX -= delta;
                }
                
                BlockPos wallPos = blockPos.offset(plateDir);
                for (Direction sideDir : Direction.values()) {
                    if (sideDir.getAxis() == plateDir.getAxis()) {continue;}
                    
                    BlockPos sidePos = wallPos.offset(sideDir);
                    BlockState sideBlockState = world.getBlockState(sidePos);
                    if (!(sideBlockState.getBlock() instanceof GravityPlatingBlock sidePlatingBlock)) {continue;}
                    
                    if (!GravityPlatingBlock.hasDir(sideBlockState, sideDir.getOpposite())) {continue;}
                    
                    if (!(world.getBlockEntity(sidePos) instanceof GravityPlatingBlockEntity be)) {continue;}
                    
                    if (isAttracting != this.isAttracting) {continue;}
                    
                    double sideDelta = getEffectRange();
                    switch (sideDir) {
                        case DOWN -> minY -= sideDelta;
                        case UP -> maxY += sideDelta;
                        case NORTH -> minZ -= sideDelta;
                        case SOUTH -> maxZ += sideDelta;
                        case WEST -> minX -= sideDelta;
                        case EAST -> maxX += sideDelta;
                    }
                }
                
                effectBoxCache = new Box(minX, minY, minZ, maxX, maxY, maxZ);
            }
            
            return effectBoxCache;
        }
    }
    
    private @Nullable SideData[] sideData = null;
    
    private @Nullable Box roughAreaBoxCache = null;
    
    @Override
    public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(tag, registries);
        
        sideData = new SideData[6];
        for (Direction dir : Direction.values()) {
            String dirName = dir.getName();
            if (tag.contains(dirName)) {
                NbtCompound sideTag = tag.getCompound(dirName);
                sideData[dir.ordinal()] = SideData.fromTag(sideTag);
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(tag, registries);
        
        if (sideData != null) {
            for (Direction dir : Direction.values()) {
                String dirName = dir.getName();
                SideData side = sideData[dir.ordinal()];
                if (side != null) {
                    tag.put(dirName, side.toTag());
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void refreshCache() {
        World world = getWorld();
        
        if (world == null) {
            LOGGER.error("Refreshing cache when world is null {}", this);
            return;
        }
        
        if (sideData == null) {
            sideData = new SideData[6];
        }
        
        BlockState blockState = world.getBlockState(this.getPos());
        for (Direction dir : Direction.values()) {
            if (GravityPlatingBlock.hasDir(blockState, dir)) {
                if (sideData[dir.ordinal()] == null) {
                    sideData[dir.ordinal()] = SideData.createDefault();
                }
            }
            else {
                sideData[dir.ordinal()] = null;
            }
        }
        
        if (this.pos.hashCode() % 5 == world.getTime() % 5) {
            roughAreaBoxCache = null;
            for (SideData sideDatum : sideData) {
                if (sideDatum != null) {
                    sideDatum.effectBoxCache = null;
                }
            }
        }
    }
    
    private Box getRoughEffectBox() {
        if (roughAreaBoxCache == null) {
            double maxRange = 0;
            for (SideData sideDatum : sideData) {
                if (sideDatum != null) {
                    maxRange = Math.max(maxRange, sideDatum.getEffectRange());
                }
            }
            
            BlockPos blockPos = this.getPos();
            double expand = 0.001;
            double delta = maxRange + expand;
            return new Box(
                blockPos.getX() - delta, blockPos.getY() - delta, blockPos.getZ() - delta,
                blockPos.getX() + 1 + delta, blockPos.getY() + 1 + delta, blockPos.getZ() + 1 + delta
            );
        }
        return roughAreaBoxCache;
    }
    
    public static void tick(World world, BlockPos blockPos, BlockState blockState, GravityPlatingBlockEntity be) {
        if (!(blockState.getBlock() instanceof GravityPlatingBlock gravityPlatingBlock)) {
            return;
        }
        
        be.refreshCache();
        
        Box roughBox = be.getRoughEffectBox();
        
        List<Entity> entities = world.getEntitiesByClass(
            Entity.class,
            roughBox,
            e -> EntityTags.canChangeGravity(e)
        );
        
        for (Entity entity : entities) {
            boolean applies = false;
            
            GravityComponent comp = GravityChangerAPI.getGravityComponent(entity);
            Direction entityGravityDir = comp.getCurrGravityDirection();
            
            for (Direction plateDir : Direction.values()) {
                SideData sideDatum = be.sideData[plateDir.ordinal()];
                if (sideDatum != null) {
                    Direction gravityEffectDir = sideDatum.isAttracting ? plateDir : plateDir.getOpposite();
                    
                    // when the player has no gravity effect and is touching the plate with their eyes,
                    // test the eye pos
                    boolean isOpposite = (entityGravityDir == gravityEffectDir.getOpposite());
                    Vec3d testingPos = isOpposite ? entity.getEyePos() : entity.getPos();
                    
                    Box gravityEffectBox = sideDatum.getEffectBox(blockPos, plateDir, world);
                    if (!gravityEffectBox.contains(testingPos)) {
                        continue;
                    }
                    
                    Vec3d plateDirVec = Vec3d.of(plateDir.getVector());
                    Vec3d effectCenter = Vec3d.ofCenter(blockPos).add(plateDirVec.multiply(0.5));
                    
                    // move the center out a little
                    // to make the distance to sharing edge different to different plates
                    double adjustment = 0.1;
                    Vec3d effectCenterAdjusted = effectCenter.add(plateDirVec.multiply(-adjustment));
                    
                    Vec3d deltaVec = testingPos.subtract(effectCenterAdjusted);
                    
                    double distanceToPlane = -deltaVec.dotProduct(plateDirVec);
                    if (distanceToPlane < -adjustment - 0.001) {
                        continue;
                    }
                    
                    Vec3d localVec = RotationUtil.vecWorldToPlayer(deltaVec, plateDir);
                    double dx = GCUtil.distanceToRange(localVec.x, -0.5, 0.5);
                    double dz = GCUtil.distanceToRange(localVec.z, -0.5, 0.5);
                    double distanceToPlate = Math.sqrt(dx * dx + dz * dz + distanceToPlane * distanceToPlane);
                    
                    double priority = 1000 - distanceToPlate;
                    if (isOpposite) {
                        // reduce the chance of opposite side plating interference
                        priority -= 10;
                    }
                    comp.applyGravityDirectionEffect(
                        gravityEffectDir, null, priority
                    );
                    applies = true;
                }
            }
            
            if (applies && GravityChangerMod.config.autoJumpOnGravityPlateInnerCorner) {
                tryToDoCornerAutoJump(blockState, blockPos, entity, comp);
            }
        }
    }
    
    // when approaching an inward corner, do auto-jump to make it smoothly go forward
    private static void tryToDoCornerAutoJump(
        BlockState blockState, BlockPos blockPos,
        Entity entity, GravityComponent comp
    ) {
        if (!entity.isOnGround()) {
            return;
        }
        
        // apply levitation when the entity is close to corner
        Direction entityGravityDir = comp.getCurrGravityDirection();
        
        for (Direction plateDir : Direction.values()) {
            if (GravityPlatingBlock.hasDir(blockState, plateDir)) {
                boolean orthogonal = entityGravityDir.getAxis() != plateDir.getAxis();
                if (!orthogonal) {
                    continue;
                }
                
                Vec3d plateDirVec = Vec3d.of(plateDir.getVector());
                
                Vec3d effectCenter = Vec3d.ofCenter(blockPos).add(plateDirVec.multiply(0.5));
                Vec3d offset = effectCenter.subtract(entity.getPos());
                if (offset.dotProduct(Vec3d.of(entityGravityDir.getVector())) > 0) {
                    // that plate is lower than entity
                    continue;
                }
                
                Vec3d worldVelocity = GravityChangerAPI.getWorldVelocity(entity);
                if (worldVelocity.dotProduct(plateDirVec) < 0.01) {
                    continue;
                }
                
                double distanceToPlate = Math.abs(entity.getPos().subtract(effectCenter).dotProduct(plateDirVec));
                if (distanceToPlate < 0.8) {
                    double strengthSqrt = Math.sqrt(comp.getCurrGravityStrength());
                    
                    Vec3d entityGravityVec = Vec3d.of(entityGravityDir.getVector());
                    
                    Vec3d deltaWorldVelocity =
                        entityGravityVec.multiply(-strengthSqrt * 0.4)
                            .add(plateDirVec.multiply(0.08));
                    
                    GravityChangerAPI.setWorldVelocity(
                        entity,
                        GravityChangerAPI.getWorldVelocity(entity).add(deltaWorldVelocity)
                    );
                    
                    if (entity.getWorld().isClient()) {
                        LOGGER.info("Client entity auto-jump on gravity plate corner {}", entity);
                    }
                    return;
                }
            }
        }
        
    }

    /*@Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

    }*/

    public ActionResult interact(World level, BlockPos pos, Direction plateDir, PlayerEntity player) {
        if (level.isClient()) {
            return ActionResult.SUCCESS;
        }
        
        refreshCache();
        
        SideData sideDatum = sideData[plateDir.ordinal()];
        
        if (sideDatum == null) {
            return ActionResult.FAIL;
        }
        
        ItemStack handItem = player.getStackInHand(Hand.MAIN_HAND);
        if (handItem.getItem() == Items.AIR) {
            // reducing world
            if (sideDatum.level != 1) {
                sideDatum.level -= 1;
                if (!player.isCreative()) {
                    player.getInventory().insertStack(new ItemStack(Items.AMETHYST_CLUSTER));
                }
            }
            else {
                sideDatum.isAttracting = !sideDatum.isAttracting;
            }
        }
        else if (handItem.getItem() == Items.AMETHYST_CLUSTER) {
            if (!player.isCreative()) {
                handItem.decrement(1);
            }
            
            sideDatum.level += 1;
            
            if (sideDatum.level > MAX_LEVEL) {
                sideDatum.level = MAX_LEVEL;
            }
        }
        else {
            ((ServerPlayerEntity) player).sendMessageToClient(
                Text.translatable("gravity_changer.plate.wrong_interaction"),
                true // on overlay (wrong parchment name)
            );
            return ActionResult.FAIL;
        }
        
        sync();
        
        boolean isAttracting = sideDatum.isAttracting;
        ((ServerPlayerEntity) player).sendMessageToClient(
            Text.translatable(
                "gravity_changer.plate.status",
                GCUtil.getDirectionText(plateDir.getOpposite()),
                sideDatum.level,
                getForceText(isAttracting)
            ),
            true // on overlay (wrong parchment name)
        );
        
        return ActionResult.SUCCESS;
    }

    public static MutableText getForceText(boolean isAttracting) {
        return Text.translatable(
            isAttracting ?
                "gravity_changer.plate.force.attract" : "gravity_changer.plate.force.repulse"
        );
    }
    
    public void sync() {
        World world = getWorld();
        Validate.notNull(world);
        Validate.isTrue(!world.isClient());
        
        markDirty();
        
        // make the packet to be sent from ChunkHolder, so the packet will be redirected by ImmPtl
        // don't directly send update packet
        ((ServerChunkManager) world.getChunkManager()).markForUpdate(this.getPos());
    }
    
    public void onPlacing(Direction side, SideData sideData) {
        refreshCache();
        this.sideData[side.ordinal()] = sideData;
        sync();
    }
    
    public List<ItemStack> getDrops() {
        if (sideData == null) {
            return List.of();
        }
        
        List<ItemStack> drops = new ArrayList<>();
        for (Direction value : Direction.values()) {
            SideData sideDatum = sideData[value.ordinal()];
            if (sideDatum != null) {
                ItemStack stack = GravityPlatingItem.createStack(sideDatum);
                drops.add(stack);
            }
        }
        return drops;
    }
}

