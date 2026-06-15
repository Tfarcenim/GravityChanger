package gravitychanger.mob_effect;

import gravitychanger.GravityChangerMod;
import gravitychanger.GravityComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class GravityStrengthMobEffect extends MobEffect {
    
    public final double base;
    public final int signum;
    
    public static final Holder<MobEffect> INCREASE =
        register("strength_increase", new GravityStrengthMobEffect(0x98D982, 1.2, 1));
    public static final Holder<MobEffect> DECREASE =
        register("strength_decrease", new GravityStrengthMobEffect(0x28D158, 0.7, 1));
    
    // it turns gravity into levitation but does not change player orientation
    public static final Holder<MobEffect> REVERSE =
        register("strength_reverse", new GravityStrengthMobEffect(0x54E972, 1.0, -1));
    
    protected GravityStrengthMobEffect(int color, double base, int signum) {
        super(MobEffectCategory.NEUTRAL, color);
        this.base = base;
        this.signum = signum;
    }
    
    public double getGravityStrengthMultiplier(int level) {
        return Math.pow(base, level) * signum;
    }
    
    private void apply(LivingEntity entity, GravityComponent component, Holder<MobEffect> effectHolder) {
        MobEffectInstance effectInstance = entity.getEffect(effectHolder);
        
        if (effectInstance == null) {
            return;
        }
        
        int level = effectInstance.getAmplifier() + 1;
    
        component.applyGravityStrengthEffect(getGravityStrengthMultiplier(level));
    }
    
    public static void init() {
        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity livingEntity) {
                GravityStrengthMobEffect increase,decrease,reverse;
                increase = (GravityStrengthMobEffect) INCREASE.value();
                decrease = (GravityStrengthMobEffect) DECREASE.value();
                reverse = (GravityStrengthMobEffect) REVERSE.value();
                increase.apply(livingEntity, component, GravityStrengthMobEffect.INCREASE);
                decrease.apply(livingEntity, component, GravityStrengthMobEffect.DECREASE);
                reverse.apply(livingEntity, component, GravityStrengthMobEffect.REVERSE);
            }
        });
    }

    private static Holder.Reference<MobEffect> register(String string, MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, string), mobEffect);
    }
}
