package gravitychanger.mob_effect;

import gravitychanger.GravityChanger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GravityInvertMobEffect extends MobEffect {
    
    public static final int COLOR = 0x98D982;
    
    public static final ResourceLocation PHASE = GravityChanger.id("invert_mob_effect_phase");

    public static final GravityInvertMobEffect INSTANCE = new GravityInvertMobEffect();
    
    private GravityInvertMobEffect() {
        super(MobEffectCategory.NEUTRAL, COLOR);
    }
    
    public static void init() {

    }
    
    
}
