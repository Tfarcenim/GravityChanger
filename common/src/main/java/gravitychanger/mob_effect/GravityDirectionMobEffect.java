package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

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
}
