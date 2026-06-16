package gravitychanger.mob_effect.refined;

import gravitychanger.GravityChangerFabric;
import gravitychanger.GravityComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class GravityStrengthStatusEffect extends MobEffect {

    public final double base;
    public final int signum;
    public static Holder<MobEffect> INCREASE_GRAVITY;
    public static Holder<MobEffect> DECREASE_GRAVITY;
    public static Holder<MobEffect> REVERSE_GRAVITY;



    protected GravityStrengthStatusEffect(int i, double base, int signum) {
        super(MobEffectCategory.NEUTRAL, i);
        this.base = base;
        this.signum = signum;
    }

    public double getGravityStrengthMultiplier(int level) {
        return Math.pow(base, level) * signum;
    }

    public static void init() {

        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity livingEntity) {
                increase.apply(livingEntity, component, INCREASE_GRAVITY);
                decrease.apply(livingEntity, component, DECREASE_GRAVITY);
                reverse.apply(livingEntity, component, REVERSE_GRAVITY);
            }
        });
    }

    private void apply(LivingEntity entity, GravityComponent component, Holder<MobEffect> effectHolder) {
        MobEffectInstance effectInstance = entity.getEffect(effectHolder);

        if (effectInstance == null) {
            return;
        }

        int level = effectInstance.getAmplifier() + 1;

        component.applyGravityStrengthEffect(getGravityStrengthMultiplier(level));
    }
}
