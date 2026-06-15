package gravitychanger.mob_effect.refined;

import gravitychanger.GravityChangerMod;
import gravitychanger.GravityComponent;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GravityInvertStatusEffect extends MobEffect {

    public static final int COLOR = 0x13c942;
    public static Holder<MobEffect> INVERT_GRAVITY;

    public static final ResourceLocation PHASE = ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "invert_mob_effect_phase");

    protected GravityInvertStatusEffect() {
        super(MobEffectCategory.NEUTRAL, COLOR);
    }

    public static void init() {

        INVERT_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "invert"),
                new GravityInvertStatusEffect());

        GravityComponent.GRAVITY_UPDATE_EVENT.register(
                PHASE, (entity, component) -> {
                    if (entity instanceof LivingEntity livingEntity) {
                        if (livingEntity.hasEffect(INVERT_GRAVITY)) {
                            component.applyGravityDirectionEffect(
                                    component.getCurrGravityDirection().getOpposite(),
                                    null, 5
                            );
                        }
                    }
                }
        );

        GravityComponent.GRAVITY_UPDATE_EVENT.addPhaseOrdering(
                GravityDirectionMobEffect.PHASE, GravityInvertMobEffect.PHASE
        );
    }
}
