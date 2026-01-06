package gravity_changer.mob_effect;

import gravity_changer.GravityComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GravityStrengthMobEffect extends StatusEffect {
    
    public final double base;
    public final int signum;
    
    public static final GravityStrengthMobEffect INCREASE =
        new GravityStrengthMobEffect(0x98D982, 1.2, 1);
    public static final GravityStrengthMobEffect DECREASE =
        new GravityStrengthMobEffect(0x98D982, 0.7, 1);
    
    // it turns gravity into levitation but does not change player orientation
    public static final GravityStrengthMobEffect REVERSE =
        new GravityStrengthMobEffect(0x98D982, 1.0, -1);
    
    protected GravityStrengthMobEffect(int color, double base, int signum) {
        super(StatusEffectCategory.NEUTRAL, color);
        this.base = base;
        this.signum = signum;
    }
    
    public double getGravityStrengthMultiplier(int level) {
        return Math.pow(base, (double) level) * signum;
    }
    
    private void apply(LivingEntity entity, GravityComponent component) {
        //TODO: probably wrong practice to change this to Registries.STATUS_EFFECT.getEntry(this)
        // aka I should probably be using something else, so look at example mod status effect later
        StatusEffectInstance effectInstance = entity.getStatusEffect(Registries.STATUS_EFFECT.getEntry(this));
        
        if (effectInstance == null) {
            return;
        }
        
        int level = effectInstance.getAmplifier() + 1;
    
        component.applyGravityStrengthEffect(getGravityStrengthMultiplier(level));
    }
    
    public static void init() {
        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity livingEntity) {
                INCREASE.apply(livingEntity, component);
                DECREASE.apply(livingEntity, component);
                REVERSE.apply(livingEntity, component);
            }
        });
        
        Registry.register(
            Registries.STATUS_EFFECT,
            Identifier.of("gravity_changer:strength_increase"),
            INCREASE
        );
        
        Registry.register(
            Registries.STATUS_EFFECT,
            Identifier.of("gravity_changer:strength_decrease"),
            DECREASE
        );
        
        Registry.register(
            Registries.STATUS_EFFECT,
            Identifier.of("gravity_changer:strength_reverse"),
            REVERSE
        );
    }
}
