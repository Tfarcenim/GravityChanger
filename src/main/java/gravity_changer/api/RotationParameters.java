package gravity_changer.api;

import gravity_changer.GravityChangerMod;
import net.minecraft.nbt.NbtCompound;

public record RotationParameters(
    boolean rotateVelocity,
    boolean rotateView, // currently ignores this
    int rotationTimeMS
) {
    public static RotationParameters defaultParam = new RotationParameters(
        true, true, 500
    );
    
    public static void updateDefault() {
        defaultParam = new RotationParameters(
            !GravityChangerMod.config.worldVelocity,
            true,
            GravityChangerMod.config.rotationTime
        );
    }
    
    public static RotationParameters getDefault() {
        return defaultParam;
    }
    
    public RotationParameters withRotationTimeMs(int rotationTimeMS) {
        return new RotationParameters(
            rotateVelocity,
            rotateView,
            rotationTimeMS
        );
    }
    
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean("RotateVelocity", rotateVelocity);
        tag.putBoolean("RotateView", rotateView);
        tag.putInt("RotationTimeMS", rotationTimeMS);
        return tag;
    }
    
    public static RotationParameters fromTag(NbtCompound tag) {
        return new RotationParameters(
            tag.getBoolean("RotateVelocity"),
            tag.getBoolean("RotateView"),
            tag.getInt("RotationTimeMS")
        );
    }
}
