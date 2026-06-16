package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public static final EnumMap<Direction, Holder<MobEffect>> EFFECT_MAP =
        new EnumMap<>(Direction.class);

    public static void init() {

    }
    
    static {
        for (Direction dir : Direction.values()) {
            Holder<MobEffect> effect = register(dir.getSerializedName(), new GravityDirectionMobEffect(dir));
            EFFECT_MAP.put(dir, effect);
        }
    }

    private static Holder.Reference<MobEffect> register(String name,MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,GravityChanger.id(name),mobEffect);
    }
}
