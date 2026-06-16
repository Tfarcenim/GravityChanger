package gravitychanger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class GravityChangerComponents implements EntityComponentInitializer{
    
    public static final ResourceLocation DATA_COMPONENT_ID =
        ResourceLocation.fromNamespaceAndPath("gravitychanger", "gravity_data");
    
    public static final org.ladysnake.cca.api.v3.component.ComponentKey<GravityComponent> GRAVITY_COMP_KEY =
        ComponentRegistry.getOrCreate(DATA_COMPONENT_ID, GravityComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
            GRAVITY_COMP_KEY, GravityComponent::new,
                (from, to, provider, lossless, keepInventory, sameCharacter) -> {
                    if (lossless || !GravityChanger.config.resetGravityOnRespawn) {
                        RespawnCopyStrategy.copy(from, to, provider);
                    }
                }
        );
        registry.registerFor(Entity.class, GRAVITY_COMP_KEY, GravityComponent::new);
    }
}
