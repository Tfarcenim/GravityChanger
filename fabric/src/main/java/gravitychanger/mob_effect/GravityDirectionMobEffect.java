package gravitychanger.mob_effect;

import gravitychanger.GravityChangerMod;
import gravitychanger.GravityComponent;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumMap;

public class GravityDirectionMobEffect extends MobEffect {
    public static final int COLOR = 0x98D982;

    public static final ResourceLocation PHASE = ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "dir_mob_effect_phase");

    public final Direction gravityDirection;

    public GravityDirectionMobEffect(Direction gravityDirection) {
        super(MobEffectCategory.NEUTRAL, COLOR);
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

    public static ResourceLocation getEffectId(Direction direction) {
        return switch (direction) {
            case DOWN -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "down");
            case UP -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "up");
            case NORTH -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "north");
            case SOUTH -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "south");
            case WEST -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "west");
            case EAST -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "east");
        };
    }

    public static void registerEffects() {
        for (Direction dir : Direction.values()) {
            Registry.register(
                    BuiltInRegistries.MOB_EFFECT,
                    getEffectId(dir),
                    EFFECT_MAP.get(dir)
            );
        }
    }

    public static void registerEvent() {
        GravityComponent.GRAVITY_UPDATE_EVENT.register(
                (entity, component) -> {
                    if (!(entity instanceof LivingEntity livingEntity)) {
                        return;
                    }

                    for (Direction dir : Direction.values()) {
                        ResourceLocation effectId = getEffectId(dir);

                        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                        Holder<MobEffect> effectHolder = Holder.direct(effect);
                        if (effect != null) {
                            MobEffectInstance effectInstance = livingEntity.getEffect(effectHolder);

                            if (effectInstance != null) {
                                int amplifier = effectInstance.getAmplifier();

                                component.applyGravityDirectionEffect(
                                        dir,
                                        null,
                                        amplifier + 1.0
                                );
                            }
                        }
                    }
                }
        );
    }
}
