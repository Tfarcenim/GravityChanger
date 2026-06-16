package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GravityInvertMobEffect extends MobEffect {
    
    public static final int COLOR = 0x98D982;
    
    public static final ResourceLocation PHASE = GravityChanger.id("invert_mob_effect_phase");

    public static final ResourceLocation ID = GravityChanger.id("invert");

    public static final Holder<MobEffect> INSTANCE =register(new GravityInvertMobEffect());
    
    private GravityInvertMobEffect() {
        super(MobEffectCategory.NEUTRAL, COLOR);
    }

    public static void init() {
    }

    private static Holder.Reference<MobEffect> register(MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,ID,mobEffect);
    }
    
}
