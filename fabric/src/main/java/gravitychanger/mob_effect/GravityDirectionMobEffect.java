package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import gravitychanger.GravityComponent;
import net.minecraft.core.Direction;
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
    
    public static final ResourceLocation PHASE = GravityChanger.id("dir_mob_effect_phase");
    
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
            case DOWN -> GravityChanger.id("down");
            case UP -> GravityChanger.id("up");
            case NORTH -> GravityChanger.id("north");
            case SOUTH -> GravityChanger.id("south");
            case WEST -> GravityChanger.id("west");
            case EAST -> GravityChanger.id("east");
        };
    }
    
    public static void init() {
        for (Direction dir : Direction.values()) {
            Registry.register(
                BuiltInRegistries.MOB_EFFECT, getEffectId(dir), EFFECT_MAP.get(dir)
            );
        }
    
        GravityComponent.GRAVITY_UPDATE_EVENT.register(
            PHASE, (entity, component) -> {
                if (!(entity instanceof LivingEntity livingEntity)) {
                    return;
                }
                
                for (GravityDirectionMobEffect dirEffect : GravityDirectionMobEffect.EFFECT_MAP.values()) {
                    MobEffectInstance effectInstance = livingEntity.getEffect(dirEffect);
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
