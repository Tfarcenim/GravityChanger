package gravitychanger.api;

import gravitychanger.*;
import gravitychanger.util.RotationUtil;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class GravityChangerAPIFabric {
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

}
