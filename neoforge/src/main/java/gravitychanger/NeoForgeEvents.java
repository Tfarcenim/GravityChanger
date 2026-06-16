package gravitychanger;

import gravitychanger.api.GravityUpdateEvent;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import gravitychanger.mob_effect.refined.GravityStrengthMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeEvents {
    static void init() {
        NeoForge.EVENT_BUS.addListener(NeoForgeEvents::updateGravityAnchor);
    }

    static void updateGravityAnchor(GravityUpdateEvent event) {
        Entity entity = event.getEntity();
        IEntityGravityData entityGravity = event.getGravity();
        if (entity instanceof LivingEntity livingEntity) {
        for (ItemStack handSlot : livingEntity.getHandSlots()) {
            Item item = handSlot.getItem();
            if (item instanceof GravityAnchorItem anchorItem) {
                event.getGravity().applyGravityDirectionEffect(
                        anchorItem.direction,
                        null, 1000000
                );
            }
        }
        }
////////////////////////////////////////////////////////////////////////////////////
        if (entity instanceof LivingEntity livingEntity) {
            GravityStrengthMobEffect.INCREASE.value().apply(livingEntity,entityGravity,GravityStrengthMobEffect.INCREASE);
            GravityStrengthMobEffect.DECREASE.value().apply(livingEntity,entityGravity,GravityStrengthMobEffect.INCREASE);
            GravityStrengthMobEffect.REVERSE.value().apply(livingEntity,entityGravity,GravityStrengthMobEffect.INCREASE);
        }
////////////////////////////////////////////////////////////////////DIRECTION
        if (entity instanceof LivingEntity livingEntity) {
            for (Holder<MobEffect> dirEffect : GravityDirectionMobEffect.EFFECT_MAP.values()) {
                MobEffectInstance effectInstance = livingEntity.getEffect(dirEffect);
                if (effectInstance != null) {
                    int amplifier = effectInstance.getAmplifier();

                    entityGravity.applyGravityDirectionEffect(
                            ((GravityDirectionMobEffect)dirEffect.value()).gravityDirection,
                            null,
                            amplifier + 1.0
                    );
                }
            }
////////////////////////////////////////////////////////////////////////////////////////////INVERT
            if (livingEntity.hasEffect(GravityInvertMobEffect.INSTANCE)) {
                entityGravity.applyGravityDirectionEffect(
                        entityGravity.getCurrGravityDirection().getOpposite(),
                        null, 5
                );
            }
        }
    }
}
