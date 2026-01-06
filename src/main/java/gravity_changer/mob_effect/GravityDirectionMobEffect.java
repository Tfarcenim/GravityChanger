package gravity_changer.mob_effect;

import gravity_changer.GravityComponent;
import java.util.EnumMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class GravityDirectionMobEffect extends StatusEffect {
    public static final int COLOR = 0x98D982;
    
    public static final Identifier PHASE = Identifier.of("gravity_changer:dir_mob_effect_phase");
    
    public final Direction gravityDirection;
    
    public GravityDirectionMobEffect(Direction gravityDirection) {
        super(StatusEffectCategory.NEUTRAL, COLOR);
        this.gravityDirection = gravityDirection;
    }
    
    public static final EnumMap<Direction, GravityDirectionMobEffect> EFFECT_MAP =
        new EnumMap<>(Direction.class);
    
    static {
        for (Direction dir : Direction.values()) {
            GravityDirectionMobEffect effect = new GravityDirectionMobEffect(dir);
            EFFECT_MAP.put(dir, effect);
        }
    }
    
    public static Identifier getEffectId(Direction direction) {
        return switch (direction) {
            case DOWN -> Identifier.of("gravity_changer:down");
            case UP -> Identifier.of("gravity_changer:up");
            case NORTH -> Identifier.of("gravity_changer:north");
            case SOUTH -> Identifier.of("gravity_changer:south");
            case WEST -> Identifier.of("gravity_changer:west");
            case EAST -> Identifier.of("gravity_changer:east");
        };
    }

    //TODO: probably wrong practice to change dirEffect to Registries.STATUS_EFFECT.getEntry(dirEffect)
    // aka I should probably be using something else, so look at example mod status effect later
    public static void init() {
        for (Direction dir : Direction.values()) {
            Registry.register(
                Registries.STATUS_EFFECT, getEffectId(dir), EFFECT_MAP.get(dir)
            );
        }
    
        GravityComponent.GRAVITY_UPDATE_EVENT.register(
            PHASE, (entity, component) -> {
                if (!(entity instanceof LivingEntity livingEntity)) {
                    return;
                }
                
                for (GravityDirectionMobEffect dirEffect : GravityDirectionMobEffect.EFFECT_MAP.values()) {
                    StatusEffectInstance effectInstance = livingEntity.getStatusEffect(Registries.STATUS_EFFECT.getEntry(dirEffect));
                    if (effectInstance != null) {
                        int amplifier = effectInstance.getAmplifier();
                        
                        component.applyGravityDirectionEffect(
                            dirEffect.gravityDirection,
                            null,
                            amplifier + 1.0
                        );
                    }
                }
            }
        );
        
    }
}
