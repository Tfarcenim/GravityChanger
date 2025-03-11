package gravitychanger;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import gravitychanger.api.RotationParameters;
import gravitychanger.util.EntityGravityData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

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
public class GravityComponent extends EntityGravityData implements Component, AutoSyncedComponent, CommonTickingComponent {
    
    public interface GravityUpdateCallback {
        void update(Entity entity, GravityComponent component);
    }

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


    public GravityComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        toNbt(tag);
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        fromNbt(nbt);
    }
    
    @Override
    public void tick() {
        commonTick();
    }

    @Override
    protected void syncEntity() {
        GravityChangerComponents.GRAVITY_COMP_KEY.sync(entity);
    }

    @Override
    protected void postEvent() {
        GRAVITY_UPDATE_EVENT.invoker().update(entity,this);
    }

    @Override
    protected void sendSyncPacketToOtherPlayers() {
        GravityChangerComponents.GRAVITY_COMP_KEY.sync(entity, this, p -> p != entity);
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);
        
        if (entity.level().isClientSide()) {
            // the packet should be handled on client thread
            // start the gravity animation (doing that during ticking is too late)
            applyGravityChange();
        }
    }
}
