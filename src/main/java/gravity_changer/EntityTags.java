package gravity_changer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EntityTags {
    /**
     * It's a whitelist for the non-living/non-projectile/non-minecart entities that can change gravity.
     * <p>
     * The main reason that it's a whitelist instead of blacklist is that,
     * the portal entities of Immersive Portals cannot change gravity.
     * Both MiniScaled and PortalGun has their own portal entity type.
     * If it's a blacklist, the maintenance of the blacklist incur more work
     * as it has to consider many different kinds of non-living mod entities.
     * It's not favorable to let every mod add blacklist entity tag for this.
     */
    public static final TagKey<EntityType<?>> ALLOWED_SPECIAL = TagKey.of(
        Registries.ENTITY_TYPE.getKey(),
        Identifier.of("gravity_changer", "allowed_special")
    );
    
    public static boolean canChangeGravity(Entity entity) {
        if (entity instanceof LivingEntity ||
            entity instanceof ProjectileEntity ||
            entity instanceof MinecartEntity
        ) {
            return true;
        }
        
        return entity.getType().getRegistryEntry().isIn(ALLOWED_SPECIAL);
    }
    
    public static boolean allowGravityTransformationInRendering(Entity entity) {
        return true;
    }
}
