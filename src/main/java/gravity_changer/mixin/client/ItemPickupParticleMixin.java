package gravity_changer.mixin.client;


import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemPickupParticle.class)
public abstract class ItemPickupParticleMixin {
    @Shadow
    @Final private Entity interactingEntity;
    
    @Shadow
    private double targetX;
    
    @Shadow
    private double targetY;
    
    @Shadow
    private double targetZ;

    //Understandable, I'll let it be until it causes problems
    /**
     * Make item absorption destination correct.
     * @author qouteall
     * @reason simpler than multiple injections
     */
    @Overwrite
    private void updateTargetPos() {
        Vec3d entityPos = interactingEntity.getPos();
        Vec3d eyePos = interactingEntity.getEyePos();
        Vec3d mid = eyePos.add(entityPos).multiply(0.5);
        
        this.targetX = mid.getX();
        this.targetY = mid.getY();
        this.targetZ = mid.getZ();
    }
}
