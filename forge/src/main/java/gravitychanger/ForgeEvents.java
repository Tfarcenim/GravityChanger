package gravitychanger;

import gravitychanger.api.GravityUpdateEvent;
import gravitychanger.api.IEntityGravityData;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import gravitychanger.mob_effect.GravityStrengthMobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEvents {
    static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::updateGravityAnchor);
    }

    static void updateGravityAnchor(GravityUpdateEvent event) {
        Entity entity = event.getEntity();
        IEntityGravityData entityGravity = event.getGravity();
        for (ItemStack handSlot : entity.getHandSlots()) {
            Item item = handSlot.getItem();
            if (item instanceof GravityAnchorItem anchorItem) {
                event.getGravity().applyGravityDirectionEffect(
                        anchorItem.direction,
                        null, 1000000
                );
            }
        }
////////////////////////////////////////////////////////////////////////////////////
        if (entity instanceof LivingEntity livingEntity) {
            GravityStrengthMobEffect.INCREASE.apply(livingEntity,entityGravity);
            GravityStrengthMobEffect.DECREASE.apply(livingEntity,entityGravity);
            GravityStrengthMobEffect.REVERSE.apply(livingEntity,entityGravity);
        }
////////////////////////////////////////////////////////////////////DIRECTION
        if (entity instanceof LivingEntity livingEntity) {
            for (GravityDirectionMobEffect dirEffect : GravityDirectionMobEffect.EFFECT_MAP.values()) {
                MobEffectInstance effectInstance = livingEntity.getEffect(dirEffect);
                if (effectInstance != null) {
                    int amplifier = effectInstance.getAmplifier();

                    entityGravity.applyGravityDirectionEffect(
                            dirEffect.gravityDirection,
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
