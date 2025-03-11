package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.EnumMap;

public class GravityPotions {
    public static Potion STRENGTH_DECR_POTION_0 = new Potion(
        new MobEffectInstance(
            GravityStrengthMobEffect.DECREASE, 9600, 0
        )
    );
    
    public static Potion STRENGTH_DECR_POTION_1 = new Potion(
        new MobEffectInstance(
            GravityStrengthMobEffect.DECREASE, 9600, 1
        )
    );
    
    public static Potion STRENGTH_INCR_POTION_0 = new Potion(
        new MobEffectInstance(
            GravityStrengthMobEffect.INCREASE, 9600, 0
        )
    );
    
    public static Potion STRENGTH_INCR_POTION_1 = new Potion(
        new MobEffectInstance(
            GravityStrengthMobEffect.INCREASE, 9600, 1
        )
    );
    
    public static final EnumMap<Direction, Potion> DIR_POTIONS = new EnumMap<>(Direction.class);
    
    static {
        for (Direction direction : Direction.values()) {
            Potion potion = new Potion(
                new MobEffectInstance(
                    GravityDirectionMobEffect.EFFECT_MAP.get(direction), 9600, 1
                )
            );
            DIR_POTIONS.put(direction, potion);
        }
    }
    
    public static ResourceLocation getPotionId(Direction direction) {
        return switch (direction) {
            case DOWN -> GravityChanger.id("gravity_down_0");
            case UP -> GravityChanger.id("gravity_up_0");
            case NORTH -> GravityChanger.id("gravity_north_0");
            case SOUTH -> GravityChanger.id("gravity_south_0");
            case WEST -> GravityChanger.id("gravity_west_0");
            case EAST -> GravityChanger.id("gravity_east_0");
        };
    }
    
    public static final Potion[] ALL = new Potion[]{
        STRENGTH_DECR_POTION_0,
        STRENGTH_DECR_POTION_1,
        STRENGTH_INCR_POTION_0,
        STRENGTH_INCR_POTION_1,
        DIR_POTIONS.get(Direction.DOWN),
        DIR_POTIONS.get(Direction.UP),
        DIR_POTIONS.get(Direction.NORTH),
        DIR_POTIONS.get(Direction.SOUTH),
        DIR_POTIONS.get(Direction.WEST),
        DIR_POTIONS.get(Direction.EAST)
    };
}
