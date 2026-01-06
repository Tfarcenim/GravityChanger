package gravity_changer.mob_effect;

import gravity_changer.GravityComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GravityInvertMobEffect extends StatusEffect {
    
    public static final int COLOR = 0x98D982;
    
    public static final Identifier PHASE = Identifier.of("gravity_changer:invert_mob_effect_phase");
    
    public static final Identifier ID = Identifier.of("gravity_changer:invert");
    
    public static final GravityInvertMobEffect INSTANCE = new GravityInvertMobEffect();
    
    private GravityInvertMobEffect() {
        super(StatusEffectCategory.NEUTRAL, COLOR);
    }

    //TODO: probably wrong practice to change INSTANCE to Registries.STATUS_EFFECT.getEntry(INSTANCE)
    // aka I should probably be using something else, so look at example mod status effect later
    public static void init() {
        GravityComponent.GRAVITY_UPDATE_EVENT.register(
            PHASE, (entity, component) -> {
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(INSTANCE))) {
                        component.applyGravityDirectionEffect(
                            component.getCurrGravityDirection().getOpposite(),
                            null, 5
                        );
                    }
                }
            }
        );
        
        // apply invert after gravity effect
        GravityComponent.GRAVITY_UPDATE_EVENT.addPhaseOrdering(
            GravityDirectionMobEffect.PHASE, GravityInvertMobEffect.PHASE
        );
        
        Registry.register(
            Registries.STATUS_EFFECT, ID, INSTANCE
        );
    }
    
    
}
