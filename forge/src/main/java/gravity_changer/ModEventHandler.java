package gravity_changer;

import gravity_changer.api.GravityUpdateEvent;
import gravity_changer.capability.EntityGravity;
import gravity_changer.item.GravityAnchorItem;
import gravity_changer.mob_effect.GravityDirectionMobEffect;
import gravity_changer.mob_effect.GravityInvertMobEffect;
import gravity_changer.mob_effect.GravityStrengthMobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModEventHandler {

    static void init() {
        MinecraftForge.EVENT_BUS.addListener(ModEventHandler::updateGravityAnchor);
    }

    static void updateGravityAnchor(GravityUpdateEvent event) {
        Entity entity = event.getEntity();
        EntityGravity entityGravity = event.getGravity();
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
            GravityStrengthMobEffect.INCREASE.apply(livingEntity);
            GravityStrengthMobEffect.DECREASE.apply(livingEntity);
            GravityStrengthMobEffect.REVERSE.apply(livingEntity);
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
