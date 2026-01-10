package gravitychanger;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.world.entity.Entity;

public class GravityChangerComponents implements EntityComponentInitializer, WorldComponentInitializer {

    public static final ComponentKey<GravityComponent> GRAVITY_COMP_KEY =
        ComponentRegistry.getOrCreate(GravityChanger.DATA_COMPONENT_ID, GravityComponent.class);

    public static final ComponentKey<DimensionGravityDataComponent> DIMENSION_COMP_KEY =
        ComponentRegistry.getOrCreate(GravityChanger.DIMENSION_DATA_ID, DimensionGravityDataComponent.class);
    
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
            GRAVITY_COMP_KEY, GravityComponent::new,
                (from, to, lossless, keepInventory, sameCharacter) -> {
                    if (lossless || !GravityChanger.config.resetGravityOnRespawn) {
                        RespawnCopyStrategy.copy(from, to);
                    }
                }
        );
        registry.registerFor(Entity.class, GRAVITY_COMP_KEY, GravityComponent::new);
    }
    
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(DIMENSION_COMP_KEY, DimensionGravityDataComponent::new);
    }
}
