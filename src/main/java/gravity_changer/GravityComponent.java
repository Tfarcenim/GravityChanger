package gravity_changer;

import com.mojang.logging.LogUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.api.RotationParameters;
import gravity_changer.mixin.EntityAccessor;
import gravity_changer.util.GCUtil;
import gravity_changer.util.RotationUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

/**
 * The gravity is determined by the follows:
 * 1. base gravity
 * 2. gravity modifier, can override base gravity (determined from modifier events)
 * 3. gravity effects, can override modified gravity
 * The result of applying 1 and 2 is called modified gravity and is synced.
 * The result of 3 is current gravity and is not synced.
 * The gravity effect should be applied both on client and server, except for remote players.
 * (The client player's gravity attributes are separately computed.
 * Other client entities' are synced from server.)
 */
public class GravityComponent implements Component, AutoSyncedComponent, CommonTickingComponent {

    public static interface GravityUpdateCallback {
        void update(Entity entity, GravityComponent component);
    }
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Fired every tick for every entity, both on client and server.
     * <p>
     * In the event, it can call
     * {@link GravityComponent#applyGravityDirectionEffect(Direction, RotationParameters, double)}
     * and
     * {@link GravityComponent#applyGravityStrengthEffect(double)}
     * (these two applying methods can also be called outside the event)
     * <p>
     * To keep the result consistent between client and server,
     * the event listener should only use synchronized information.
     * <p>
     * In the event, it can read the current gravity direction for use cases like gravity inverting. (It requires phase ordering to keep the execution order.)
     */
    public static final Event<GravityUpdateCallback> GRAVITY_UPDATE_EVENT =
        EventFactory.createArrayBacked(
            GravityUpdateCallback.class,
            listeners -> (entity, component) -> {
                for (GravityUpdateCallback callback : listeners) {
                    callback.update(entity, component);
                }
            }
        );
    
    private boolean initialized = false;
    
    // not synchronized
    private Direction prevGravityDirection = Direction.DOWN;
    private double prevGravityStrength = 1.0;
    
    // the base gravity direction
    Direction baseGravityDirection = Direction.DOWN;
    
    // the base gravity strength
    double baseGravityStrength = 1.0;
    
    @Nullable RotationParameters currentRotationParameters = RotationParameters.getDefault();
    
    // Only used on client, not synchronized.
    @Nullable
    public final RotationAnimation animation;
    
    public final Entity entity;
    
    private Direction currGravityDirection = Direction.DOWN;
    private double currGravityStrength = 1.0;
    private double currentEffectPriority = Double.MIN_VALUE;
    
    private boolean isFiringUpdateEvent = false;
    
    private @Nullable GravityComponent.GravityDirEffect delayApplyDirEffect = null;
    private double delayApplyStrengthEffect = 1.0;
    
    // if it equals entity.tickCount,
    // it means that the gravity update event has already fired in this tick
    private long lastUpdateTickCount = 0;
    
    // only used on server side
    private boolean needsSync = false;
    
    public GravityComponent(Entity entity) {
        this.entity = entity;
        if (entity.getWorld().isClient()) {
            animation = new RotationAnimation();
        }
        else {
            animation = null;
        }
    }
    
    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains("baseGravityDirection")) {
            baseGravityDirection = Direction.byName(nbtCompound.getString("baseGravityDirection"));
        }
        else {
            baseGravityDirection = Direction.DOWN;
        }
        
        if (nbtCompound.contains("baseGravityStrength")) {
            baseGravityStrength = nbtCompound.getDouble("baseGravityStrength");
        }
        else {
            baseGravityStrength = 1.0;
        }
        
        // the current gravity is serialized to avoid unnecessary gravity rotation when entering world
        // do not deserialize it when for client player when not initializing
        if (!initialized || shouldAcceptServerSync()) {
            if (nbtCompound.contains("currentGravityDirection")) {
                currGravityDirection = Direction.byName(nbtCompound.getString("currentGravityDirection"));
            }
            else {
                currGravityDirection = Direction.DOWN;
            }
            
            if (nbtCompound.contains("currentGravityStrength")) {
                currGravityStrength = nbtCompound.getDouble("currentGravityStrength");
            }
            else {
                currGravityStrength = 1.0;
            }
        }
        
        if (!initialized) {
            prevGravityDirection = currGravityDirection;
            prevGravityStrength = currGravityStrength;
            initialized = true;
            applyGravityDirectionChange(
                prevGravityDirection, currGravityDirection, currentRotationParameters, true
            );
        }
    }
    
    private boolean shouldAcceptServerSync() {
        return entity.getWorld().isClient() && !GCUtil.isClientPlayer(entity);
    }
    
    @Override
    public void writeToNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putString("baseGravityDirection", baseGravityDirection.getName());
        nbtCompound.putString("currentGravityDirection", currGravityDirection.getName());

        nbtCompound.putDouble("baseGravityStrength", baseGravityStrength);
        nbtCompound.putDouble("currentGravityStrength", currGravityStrength);
    }
    
    @Override
    public void tick() {
        if (!canChangeGravity()) {
            return;
        }
        
        updateGravityStatus(true);
        
        applyGravityChange();
        
        if (!entity.getWorld().isClient()) {
            if (needsSync) {
                needsSync = false;
                GravityChangerComponents.GRAVITY_COMP_KEY.sync(entity);
            }
        }
    }
    
    public void updateGravityStatus(boolean sendPacketIfNecessary) {
        // for the remote players and non-player entities,
        // their effect data is not synchronized to the client
        // (possibly for making it harder to cheat for hacked clients)
        // then we don't calculate its gravity in normal way in client
        if (shouldAcceptServerSync()) {
            return;
        }
        
        Direction oldGravityDirection = currGravityDirection;
        double oldGravityStrength = currGravityStrength;
        
        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            currGravityDirection = GravityChangerAPI.getGravityDirection(vehicle);
            currGravityStrength = GravityChangerAPI.getGravityStrength(vehicle);
        }
        else {
            currGravityDirection = baseGravityDirection;
            currGravityStrength = baseGravityStrength;
            currGravityStrength *= GravityChangerAPI.getDimensionGravityStrength(entity.getWorld());
            currGravityStrength *= GravityChangerMod.config.gravityStrengthMultiplier;
            // the rotation parameters is not being reset here
            // the rotation parameter is kept when an effect vanishes
            currentEffectPriority = Double.MIN_VALUE;
            
            isFiringUpdateEvent = true;
            try {
                GRAVITY_UPDATE_EVENT.invoker().update(entity, this);
                if (delayApplyDirEffect != null) {
                    applyGravityDirectionEffect(
                        delayApplyDirEffect.direction(),
                        delayApplyDirEffect.rotationParameters(), delayApplyDirEffect.priority()
                    );
                    delayApplyDirEffect = null;
                }
                currGravityStrength *= delayApplyStrengthEffect;
                delayApplyStrengthEffect = 1.0;
            }
            finally {
                isFiringUpdateEvent = false;
            }
            
            if (currentEffectPriority == Double.MIN_VALUE) {
                // if no effect is applied, reset the rotation parameters
                currentRotationParameters = RotationParameters.getDefault();
            }
            
            lastUpdateTickCount = entity.age;
        }
        
        if (sendPacketIfNecessary) {
            boolean changed = oldGravityDirection != currGravityDirection ||
                Math.abs(oldGravityStrength - currGravityStrength) > 0.0001;
            if (changed) {
                sendSyncPacketToOtherPlayers();
            }
        }
    }
    
    private void sendSyncPacketToOtherPlayers() {
        GravityChangerComponents.GRAVITY_COMP_KEY.sync(entity, this, p -> p != entity);
    }
    
    public void applyGravityDirectionEffect(
        @NotNull Direction direction,
        @Nullable RotationParameters rotationParameters,
        double priority
    ) {
        if (isFiringUpdateEvent) {
            if (priority > currentEffectPriority) {
                currentEffectPriority = priority;
                currGravityDirection = direction;
                
                if (rotationParameters != null) {
                    currentRotationParameters = rotationParameters;
                }
            }
        }
        else {
            // When not firing event, store it on delayApplyEffect.
            // The effect could come from another entity ticking,
            // but there is no guarantee for ticking order between entities.
            // (the ticking order does not change according to EntityTickList)
            if (delayApplyDirEffect == null || priority > delayApplyDirEffect.priority()) {
                delayApplyDirEffect = new GravityDirEffect(
                    direction, rotationParameters, priority
                );
            }
        }
    }
    
    public void applyGravityStrengthEffect(
        double strengthMultiplier
    ) {
        if (isFiringUpdateEvent) {
            currGravityStrength *= strengthMultiplier;
        }
        else {
            delayApplyStrengthEffect *= strengthMultiplier;
        }
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);

        if (entity.getWorld().isClient()) {
            // the packet should be handled on client thread
            // start the gravity animation (doing that during ticking is too late)
            applyGravityChange();
        }
    }

    /*@Override
    public void applySyncPacket(PacketByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);

        if (entity.getWorld().isClient()) {
            // the packet should be handled on client thread
            // start the gravity animation (doing that during ticking is too late)
            applyGravityChange();
        }
    }*/
    
    public void applyGravityDirectionChange(
        Direction oldGravity, Direction newGravity,
        RotationParameters rotationParameters, boolean isInitialization
    ) {
        if (!canChangeGravity()) {
            return;
        }
        
        // update bounding box
        entity.setBoundingBox(((EntityAccessor) entity).gc_calculateBoundingBox());
        
        // A weird thing is that,
        // using `entity.setPos(entity.getPos())` to a painting on client side
        // make the painting move wrongly, because Painting overrides `trackingPosition()`.
        // No entity other than Painting overrides that method.
        // It seems to be legacy code from early versions of Minecraft.
        
        if (isInitialization) {
            return;
        }
        
        entity.fallDistance = 0;
        
        long timeMs = entity.getWorld().getTime() * 50;
        
        Vec3d relativeRotationCenter = getLocalRotationCenter(
            entity, oldGravity, newGravity, rotationParameters
        );
        Vec3d oldPos = entity.getPos();
        Vec3d oldLastTickPos = new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ);
        Vec3d rotationCenter = oldPos.add(RotationUtil.vecPlayerToWorld(relativeRotationCenter, oldGravity));
        Vec3d newPos = rotationCenter.subtract(RotationUtil.vecPlayerToWorld(relativeRotationCenter, newGravity));
        Vec3d posTranslation = newPos.subtract(oldPos);
        Vec3d newLastTickPos = oldLastTickPos.add(posTranslation);
        
        entity.setPosition(newPos);
        entity.prevX = newLastTickPos.x;
        entity.prevY = newLastTickPos.y;
        entity.prevZ = newLastTickPos.z;
        entity.lastRenderX = newLastTickPos.x;
        entity.lastRenderY = newLastTickPos.y;
        entity.lastRenderZ = newLastTickPos.z;
        
        adjustEntityPosition(oldGravity, newGravity, entity.getBoundingBox());
        
        if (entity.getWorld().isClient()) {
            Validate.notNull(animation, "gravity animation is null");
            
            int rotationTimeMS = rotationParameters.rotationTimeMS();
            
            animation.startRotationAnimation(
                newGravity, oldGravity,
                rotationTimeMS,
                entity, timeMs, rotationParameters.rotateView(),
                relativeRotationCenter
            );
        }
        
        Vec3d realWorldVelocity = getRealWorldVelocity(entity, oldGravity);
        if (rotationParameters.rotateVelocity()) {
            // Rotate velocity with gravity, this will cause things to appear to take a sharp turn
            Vector3f worldSpaceVec = realWorldVelocity.toVector3f();
            worldSpaceVec.rotate(RotationUtil.getRotationBetween(oldGravity, newGravity));
            entity.setVelocity(RotationUtil.vecWorldToPlayer(new Vec3d(worldSpaceVec), newGravity));
        }
        else {
            // Velocity will be conserved relative to the world, will result in more natural motion
            entity.setVelocity(RotationUtil.vecWorldToPlayer(realWorldVelocity, newGravity));
        }
    }
    
    // getVelocity() does not return the actual velocity. It returns the velocity plus acceleration.
    // Even if the entity is standing still, getVelocity() will still give a downwards vector.
    // The real velocity is this tick getPos subtract last tick getPos
    private static Vec3d getRealWorldVelocity(Entity entity, Direction prevGravityDirection) {
        if (entity.isLogicalSideForUpdatingMovement()) {
            return new Vec3d(
                entity.getX() - entity.prevX,
                entity.getY() - entity.prevY,
                entity.getZ() - entity.prevZ
            );
        }
        
        return RotationUtil.vecPlayerToWorld(entity.getVelocity(), prevGravityDirection);
    }
    
    @NotNull
    private static Vec3d getLocalRotationCenter(
        Entity entity,
        Direction oldGravity, Direction newGravity, RotationParameters rotationParameters
    ) {
        if (entity instanceof EndCrystalEntity) {
            //In the middle of the block below
            return new Vec3d(0, -0.5, 0);
        }
        
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        if (newGravity.getOpposite() == oldGravity) {
            // In the center of the hit-box
            return new Vec3d(0, dimensions.height() / 2, 0);
        }
        else {
            return Vec3d.ZERO;
        }
    }
    
    // Adjust getPos to avoid suffocation in blocks when changing gravity
    private void adjustEntityPosition(Direction oldGravity, Direction newGravity, Box entityBoundingBox) {
        if (!GravityChangerMod.config.adjustPositionAfterChangingGravity) {
            return;
        }
        
        if (entity instanceof AreaEffectCloudEntity || entity instanceof PersistentProjectileEntity || entity instanceof EndCrystalEntity) {
            return;
        }
        
        // for example, if gravity changed from down to north, move up
        // if gravity changed from down to up, also move up
        Direction movingDirection = oldGravity.getOpposite();
        
        Iterable<VoxelShape> collisions = entity.getWorld().getCollisions(
            entity,
            entityBoundingBox.expand(-0.01) // shrink to avoid floating point error
        );
        Box totalCollisionBox = null;
        for (VoxelShape collision : collisions) {
            if (!collision.isEmpty()) {
                Box boundingBox = collision.getBoundingBox();
                if (totalCollisionBox == null) {
                    totalCollisionBox = boundingBox;
                }
                else {
                    totalCollisionBox = totalCollisionBox.union(boundingBox);
                }
            }
        }
        
        if (totalCollisionBox != null) {
            Vec3d positionAdjustmentOffset = getPositionAdjustmentOffset(
                entityBoundingBox, totalCollisionBox, movingDirection
            );
            if (entity instanceof PlayerEntity) {
                LOGGER.info("Adjusting player getPos {} {}", positionAdjustmentOffset, entity);
            }
            entity.setPosition(entity.getPos().add(positionAdjustmentOffset));
        }
    }
    
    private static Vec3d getPositionAdjustmentOffset(
        Box entityBoundingBox, Box nearbyCollisionUnion, Direction movingDirection
    ) {
        Direction.Axis axis = movingDirection.getAxis();
        double offset = 0;
        if (movingDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            double pushing = nearbyCollisionUnion.getMax(axis);
            double pushed = entityBoundingBox.getMin(axis);
            if (pushing > pushed) {
                offset = pushing - pushed;
            }
        }
        else {
            double pushing = nearbyCollisionUnion.getMin(axis);
            double pushed = entityBoundingBox.getMax(axis);
            if (pushing < pushed) {
                offset = pushed - pushing;
            }
        }
        
        return new Vec3d(movingDirection.getUnitVector()).multiply(offset);
    }
    
    public double getBaseGravityStrength() {
        return baseGravityStrength;
    }
    
    public void setBaseGravityStrength(double strength) {
        if (!canChangeGravity()) {
            return;
        }
        
        baseGravityStrength = strength;
        needsSync = true;
    }
    
    public Direction getCurrGravityDirection() {
        return currGravityDirection;
    }
    
    public double getCurrGravityStrength() {
        return currGravityStrength;
    }
    
    private boolean canChangeGravity() {
        return EntityTags.canChangeGravity(entity);
    }
    
    public Direction getPrevGravityDirection() {
        return prevGravityDirection;
    }
    
    public Direction getBaseGravityDirection() {
        return baseGravityDirection;
    }
    
    public void setBaseGravityDirection(Direction gravityDirection) {
        if (!canChangeGravity()) {
            return;
        }
        
        if (baseGravityDirection != gravityDirection) {
            baseGravityDirection = gravityDirection;
            needsSync = true;
            
            // update gravity immediately
            // avoid having wrong info from getGravityDirection()
            updateGravityStatus(false); // will this cause issue?
        }
    }
    
    public void reset() {
        baseGravityDirection = Direction.DOWN;
        baseGravityStrength = 1.0;
        needsSync = true;
    }
    
    @Environment(EnvType.CLIENT)
    public RotationAnimation getRotationAnimation() {
        return animation;
    }
    
    public void applyGravityChange() {
        if (currentRotationParameters == null) {
            currentRotationParameters = RotationParameters.getDefault();
        }
        
        if (prevGravityDirection != currGravityDirection) {
            applyGravityDirectionChange(
                prevGravityDirection, currGravityDirection,
                currentRotationParameters, false
            );
            prevGravityDirection = currGravityDirection;
        }
        
        if (Math.abs(currGravityStrength - prevGravityStrength) > 0.0001) {
            prevGravityStrength = currGravityStrength;
        }
    }
    
    /**
     * Not needed in normal cases.
     * Only used in {@link GravityChangerAPI#instantlySetClientBaseGravityDirection(Entity, Direction)}
     * Used by ImmPtl.
     */
    public void forceApplyGravityChange() {
        prevGravityDirection = currGravityDirection;
        prevGravityStrength = currGravityStrength;
    }
    
    private static record GravityDirEffect(
        @NotNull Direction direction,
        @Nullable RotationParameters rotationParameters,
        double priority
    ) {
    
    }
}
