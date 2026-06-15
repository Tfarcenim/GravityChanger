package gravitychanger.mob_effect.refined;

import gravitychanger.GravityChangerMod;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.EnumMap;

public class GravityPotions {

    public static Potion STRENGTH_DECR_POTION_0 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.DECREASE_GRAVITY, 9600, 0
            )
    );

    public static Potion STRENGTH_DECR_POTION_1 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.DECREASE_GRAVITY, 9600, 1
            )
    );

    public static Potion STRENGTH_INCR_POTION_0 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.INCREASE_GRAVITY, 9600, 0
            )
    );

    public static Potion STRENGTH_INCR_POTION_1 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.INCREASE_GRAVITY, 9600, 1
            )
    );

    public static Potion STRENGTH_REVERSE_POTION_0 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.REVERSE_GRAVITY, 9600, 0
            )
    );

    public static Potion STRENGTH_REVERSE_POTION_1 = new Potion(
            new MobEffectInstance(
                    GravityStrengthStatusEffect.REVERSE_GRAVITY, 9600, 1
            )
    );

    public static Potion STRENGTH_INVERT_POTION_0 = new Potion(
            new MobEffectInstance(
                    GravityInvertStatusEffect.INVERT_GRAVITY, 9600, 1
            )
    );

    public static final EnumMap<Direction, Potion> DIR_POTIONS = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.values()) {
            Potion potion = new Potion(
                    new MobEffectInstance(
                            GravityDirectionStatusEffect.getEffectInstance(direction), 9600, 0
                    )
            );
            DIR_POTIONS.put(direction, potion);
        }
    }

    public static ResourceLocation getPotionId(Direction direction) {
        return switch (direction) {
            case DOWN -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_down_0");
            case UP -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_up_0");
            case NORTH -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_north_0");
            case SOUTH -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_south_0");
            case WEST -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_west_0");
            case EAST -> ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_east_0");
        };
    }

    public static void init() {
        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_decr_0"),
                STRENGTH_DECR_POTION_0
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_decr_1"),
                STRENGTH_DECR_POTION_1
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_incr_0"),
                STRENGTH_INCR_POTION_0
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_incr_1"),
                STRENGTH_INCR_POTION_1
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_reverse_0"),
                STRENGTH_REVERSE_POTION_0
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_reverse_1"),
                STRENGTH_REVERSE_POTION_1
        );

        Registry.registerForHolder(
                BuiltInRegistries.POTION,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "gravity_invert_0"),
                STRENGTH_INVERT_POTION_0
        );

        for (Direction direction : Direction.values()) {
            Potion potion = DIR_POTIONS.get(direction);
            Registry.registerForHolder(
                    BuiltInRegistries.POTION,
                    getPotionId(direction),
                    potion
            );
        }
    }

    public static final Potion[] ALL = new Potion[] {
            STRENGTH_DECR_POTION_0,
            STRENGTH_DECR_POTION_1,
            STRENGTH_INCR_POTION_0,
            STRENGTH_INCR_POTION_1,
            STRENGTH_REVERSE_POTION_0,
            STRENGTH_REVERSE_POTION_1,
            STRENGTH_INVERT_POTION_0,
            DIR_POTIONS.get(Direction.DOWN),
            DIR_POTIONS.get(Direction.UP),
            DIR_POTIONS.get(Direction.NORTH),
            DIR_POTIONS.get(Direction.SOUTH),
            DIR_POTIONS.get(Direction.WEST),
            DIR_POTIONS.get(Direction.EAST)
    };
}
