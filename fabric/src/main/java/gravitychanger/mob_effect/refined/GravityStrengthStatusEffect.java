package gravitychanger.mob_effect.refined;

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

        GravityStrengthStatusEffect increase = new GravityStrengthStatusEffect(0x98D982, 2.2, 1);

        GravityStrengthStatusEffect decrease = new GravityStrengthStatusEffect(0x28D982, 0.25, 1);

        GravityStrengthStatusEffect reverse = new GravityStrengthStatusEffect(0x58D982, 1.0, -1);

        INCREASE_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "strength_increase"),
                increase);

        DECREASE_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "strength_decrease"),
                decrease);

        REVERSE_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "strength_reverse"),
                reverse);

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
