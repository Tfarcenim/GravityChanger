package gravity_changer.unused.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin_2 extends Entity {

    public AreaEffectCloudEntityMixin_2(EntityType<?> type, World world) { super(type, world); }

    @Shadow
    public abstract ParticleEffect getParticleType();

    /*@Shadow
    public abstract int getColor();*/

    @Shadow
    private int duration;
    @Shadow
    private int waitTime;

    @Shadow
    protected abstract void setWaiting(boolean waiting);

    @Shadow
    private float radiusGrowth;

    @Shadow
    public abstract void setRadius(float radius);

    @Shadow
    @Final
    private Map<Entity, Integer> affectedEntities;
    /*@Shadow
    private Potion potion;
    @Shadow
    @Final
    private List<StatusEffectInstance> effects;*/
    @Shadow
    private int reapplicationDelay;

    @Shadow
    @Nullable
    public abstract LivingEntity getOwner();

    @Shadow
    private float radiusOnUse;
    @Shadow
    private int durationOnUse;
    //private static final TrackedData<Direction> gravitychanger$GRAVITY_DIRECTION = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FACING);

    //private static final TrackedData<Direction> gravitychanger$DEFAULT_GRAVITY_DIRECTION = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FACING);

    //private Direction gravitychanger$prevGravityDirection = Direction.DOWN;

    /*@Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        Entity vehicle = this.getVehicle();
        if(vehicle != null) {
            return GravityChangerAPI.getGravityDirection(vehicle);
        }

        return GravityChangerAPI.getGravityDirection((AreaEffectCloudEntity)(Object)this);
    }*/
//
//
//
    //  @Override
    //  public void gravitychanger$onGravityChanged(Direction prevGravityDirection, boolean initialGravity) {
    //      Direction gravityDirection = this.gravitychanger$getGravityDirection();
//
    //      this.fallDistance = 0;
//
    //      this.setBoundingBox(this.calculateBoundingBox());
//
    //      if(!initialGravity) {
    //          // Adjust getPos to avoid suffocation in blocks when changing gravity
    //          EntityDimensions dimensions = this.getDimensions(this.getPose());
    //          Direction relativeDirection = RotationUtil.dirWorldToPlayer(gravityDirection, prevGravityDirection);
    //          Vec3d relativePosOffset = switch(relativeDirection) {
    //              case DOWN -> Vec3d.ZERO;
    //              case UP -> new Vec3d(0.0D, dimensions.height - 1.0E-6D, 0.0D);
    //              default -> Vec3d.of(relativeDirection.getVector()).multiply(dimensions.width / 2 - (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D)).add(0.0D, dimensions.width / 2 - (prevGravityDirection.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0E-6D : 0.0D), 0.0D);
    //          };
    //          //this.setPosition(this.getPos().add(RotationUtil.vecPlayerToWorld(relativePosOffset, prevGravityDirection)));
    //          if(GravityChangerMod.config.worldVelocity)
    //          this.setVelocity(RotationUtil.vecWorldToPlayer(RotationUtil.vecPlayerToWorld(this.getVelocity(), prevGravityDirection), gravityDirection));
//
//
//
    //      }
    //  }
//
    //  @Override
    //  public Direction gravitychanger$getTrackedGravityDirection() {
    //      return this.getDataTracker().get(gravitychanger$GRAVITY_DIRECTION);
    //  }
//
    //  @Override
    //  public void gravitychanger$setTrackedGravityDirection(Direction gravityDirection) {
    //      this.getDataTracker().set(gravitychanger$GRAVITY_DIRECTION, gravityDirection);
    //  }
//
//
    //  @Override
    //  public Direction gravitychanger$getDefaultTrackedGravityDirection() {
    //      return this.getDataTracker().get(gravitychanger$DEFAULT_GRAVITY_DIRECTION);
    //  }
//
    //  @Override
    //  public void gravitychanger$setDefaultTrackedGravityDirection(Direction gravityDirection) {
    //      this.getDataTracker().set(gravitychanger$DEFAULT_GRAVITY_DIRECTION, gravityDirection);
    //  }
//
    //  @Override
    //  public void gravitychanger$onTrackedData(TrackedData<?> data) {
    //      if(!this.world.isClient) return;
//
    //      if(gravitychanger$GRAVITY_DIRECTION.equals(data)) {
    //          Direction gravityDirection = this.gravitychanger$getGravityDirection();
    //          if(this.gravitychanger$prevGravityDirection != gravityDirection) {
    //              this.gravitychanger$onGravityChanged(this.gravitychanger$prevGravityDirection, true);
    //              this.gravitychanger$prevGravityDirection = gravityDirection;
    //          }
    //      }
    //  }
//
    //  @Inject(
    //          method = "initDataTracker",
    //          at = @At("RETURN")
    //  )
    //  private void inject_initDataTracker(CallbackInfo ci) {
    //      this.dataTracker.startTracking(gravitychanger$GRAVITY_DIRECTION, Direction.DOWN);
    //      this.dataTracker.startTracking(gravitychanger$DEFAULT_GRAVITY_DIRECTION, Direction.DOWN);
    //  }
//
    //  @Inject(
    //          method = "readCustomDataFromNbt",
    //          at = @At("RETURN")
    //  )
    //  private void inject_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
    //      if(nbt.contains("GravityDirection", NbtElement.INT_TYPE)) {
    //          Direction gravityDirection = Direction.byId(nbt.getInt("GravityDirection"));
    //          this.gravitychanger$setGravityDirection(gravityDirection, true);
    //      }
    //      if(nbt.contains("DefaultGravityDirection", NbtElement.INT_TYPE)) {
    //          Direction gravityDirection = Direction.byId(nbt.getInt("DefaultGravityDirection"));
    //          this.gravitychanger$setDefaultGravityDirection(gravityDirection, true);
    //      }
    //  }
//
    //  @Inject(
    //          method = "writeCustomDataToNbt",
    //          at = @At("RETURN")
    //  )
    //  private void inject_writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
    //      nbt.putInt("GravityDirection", this.gravitychanger$getGravityDirection().getId());
    //      nbt.putInt("DefaultGravityDirection", this.gravitychanger$getDefaultGravityDirection().getId());
    //  }
}
