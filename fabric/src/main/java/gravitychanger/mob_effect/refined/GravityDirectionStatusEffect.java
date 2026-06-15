package gravitychanger.mob_effect.refined;

import gravitychanger.GravityChangerMod;
import gravitychanger.GravityComponent;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GravityDirectionStatusEffect extends MobEffect {

    private final Direction direction;
    public static Holder<MobEffect> NORTH_GRAVITY;
    public static Holder<MobEffect> SOUTH_GRAVITY;
    public static Holder<MobEffect> WEST_GRAVITY;
    public static Holder<MobEffect> EAST_GRAVITY;
    public static Holder<MobEffect> UP_GRAVITY;
    public static Holder<MobEffect> DOWN_GRAVITY;


    protected GravityDirectionStatusEffect(int i, Direction direction) {
        super(MobEffectCategory.NEUTRAL, i);
        this.direction = direction;
    }

    public static void init() {
        NORTH_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "north"),
                new GravityDirectionStatusEffect(0xe0ece9, Direction.NORTH));
        SOUTH_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "south"),
                new GravityDirectionStatusEffect(0xbbd2ec, Direction.SOUTH));
        WEST_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "west"),
                new GravityDirectionStatusEffect(0x7775e7, Direction.WEST));
        EAST_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "east"),
                new GravityDirectionStatusEffect(0xd239fd, Direction.EAST));
        UP_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "up"),
                new GravityDirectionStatusEffect(0x91a285, Direction.UP));
        DOWN_GRAVITY = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "down"),
                new GravityDirectionStatusEffect(0xfd3939, Direction.DOWN));


        GravityComponent.GRAVITY_UPDATE_EVENT.register((entity, component) -> {
            if (entity instanceof LivingEntity mob) {
                if (mob.hasEffect(NORTH_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.NORTH,
                            null, 1000000
                    );
                if (mob.hasEffect(SOUTH_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.SOUTH,
                            null, 1000000
                    );
                if (mob.hasEffect(WEST_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.WEST,
                            null, 1000000
                    );
                if (mob.hasEffect(EAST_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.EAST,
                            null, 1000000
                    );
                if (mob.hasEffect(UP_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.UP,
                            null, 1000000
                    );
                if (mob.hasEffect(DOWN_GRAVITY))
                    component.applyGravityDirectionEffect(
                            Direction.DOWN,
                            null, 1000000
                    );
            }
        });
    }

    public static Holder<MobEffect> getEffectInstance(Direction direction) {
        switch (direction) {
            case NORTH -> {
                return NORTH_GRAVITY;
            }case SOUTH -> {
                return SOUTH_GRAVITY;
            }case WEST -> {
                return WEST_GRAVITY;
            }case EAST -> {
                return EAST_GRAVITY;
            }case UP -> {
                return UP_GRAVITY;
            }default -> {
                return DOWN_GRAVITY;
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}
