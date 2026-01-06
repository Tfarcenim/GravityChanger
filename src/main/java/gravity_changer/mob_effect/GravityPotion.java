package gravity_changer.mob_effect;

import java.util.EnumMap;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class GravityPotion {
    //TODO: probably wrong practice to change <effect> to Registries.STATUS_EFFECT.getEntry(<effect>)
    // aka I should probably be using something else, so look at example mod status effect later

    public static Potion STRENGTH_DECR_POTION_0 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.DECREASE), 9600, 0
        )
    );
    
    public static Potion STRENGTH_DECR_POTION_1 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.DECREASE), 9600, 1
        )
    );
    
    public static Potion STRENGTH_INCR_POTION_0 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.INCREASE), 9600, 0
        )
    );
    
    public static Potion STRENGTH_INCR_POTION_1 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.INCREASE), 9600, 1
        )
    );
    
    public static Potion STRENGTH_REVERSE_POTION_0 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.REVERSE), 9600, 0
        )
    );
    
    public static Potion STRENGTH_REVERSE_POTION_1 = new Potion(
        new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(GravityStrengthMobEffect.REVERSE), 9600, 1
        )
    );
    
    public static final EnumMap<Direction, Potion> DIR_POTIONS = new EnumMap<>(Direction.class);
    
    static {
        for (Direction direction : Direction.values()) {
            Potion potion = new Potion(
                new StatusEffectInstance(
                        Registries.STATUS_EFFECT.getEntry(GravityDirectionMobEffect.EFFECT_MAP.get(direction)), 9600, 1
                )
            );
            DIR_POTIONS.put(direction, potion);
        }
    }
    
    public static Identifier getPotionId(Direction direction) {
        return switch (direction) {
            case DOWN -> Identifier.of("gravity_changer:gravity_down_0");
            case UP -> Identifier.of("gravity_changer:gravity_up_0");
            case NORTH -> Identifier.of("gravity_changer:gravity_north_0");
            case SOUTH -> Identifier.of("gravity_changer:gravity_south_0");
            case WEST -> Identifier.of("gravity_changer:gravity_west_0");
            case EAST -> Identifier.of("gravity_changer:gravity_east_0");
        };
    }
    
    public static final Potion[] ALL = new Potion[]{
        STRENGTH_DECR_POTION_0,
        STRENGTH_DECR_POTION_1,
        STRENGTH_INCR_POTION_0,
        STRENGTH_INCR_POTION_1,
        STRENGTH_REVERSE_POTION_0,
        STRENGTH_REVERSE_POTION_1,
        DIR_POTIONS.get(Direction.DOWN),
        DIR_POTIONS.get(Direction.UP),
        DIR_POTIONS.get(Direction.NORTH),
        DIR_POTIONS.get(Direction.SOUTH),
        DIR_POTIONS.get(Direction.WEST),
        DIR_POTIONS.get(Direction.EAST)
    };
    
    public static void init() {
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_decr_0"),
            STRENGTH_DECR_POTION_0
        );
        
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_decr_1"),
            STRENGTH_DECR_POTION_1
        );
        
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_incr_0"),
            STRENGTH_INCR_POTION_0
        );
        
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_incr_1"),
            STRENGTH_INCR_POTION_1
        );
        
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_reverse_0"),
            STRENGTH_REVERSE_POTION_0
        );
        
        Registry.register(
            Registries.POTION,
            Identifier.of("gravity_changer:gravity_reverse_1"),
            STRENGTH_REVERSE_POTION_1
        );
        
        for (Direction direction : Direction.values()) {
            Potion potion = DIR_POTIONS.get(direction);
            Registry.register(
                Registries.POTION,
                getPotionId(direction),
                potion
            );
        }
    }
}
