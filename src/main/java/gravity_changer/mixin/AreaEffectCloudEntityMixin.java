package gravity_changer.mixin;

import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin extends Entity {

    public AreaEffectCloudEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow
    public abstract boolean isWaiting();
    
    @Shadow
    public abstract float getRadius();

    //Implementation changed, but seems to do the same thing
    @ModifyArgs(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addImportantParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
        )
    )
    private void modify_move_multiply_0(Args args) {
        boolean bl = this.isWaiting();
        float f = this.getRadius();
        
        float g;
        if (bl) {
            g = 0.2F;
        }
        else {
            g = f;
        }
        
        float h = this.random.nextFloat() * 6.2831855F;
        float k = MathHelper.sqrt(this.random.nextFloat()) * g;
        
        double d = this.getX();
        double e = this.getY();
        double l = this.getZ();
        Vec3d modify = RotationUtil.vecWorldToPlayer(d, e, l, GravityChangerAPI.getGravityDirection(this));
        d = modify.x + (double) (MathHelper.cos(h) * k);
        e = modify.y;
        l = modify.z + (double) (MathHelper.sin(h) * k);
        modify = RotationUtil.vecPlayerToWorld(d, e, l, GravityChangerAPI.getGravityDirection(this));
        
        args.set(1, modify.x);
        args.set(2, modify.y);
        args.set(3, modify.z);
    }


}
