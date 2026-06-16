package gravitychanger.attachments;

import gravitychanger.platform.Services;
import net.minecraft.world.level.Level;

public class DataAttachmentUtil {

    public static double getLevelGravityData(Level world) {
        return Services.PLATFORM.getOrCreateAttachedValue(world,CommonDataAttachments.DIMENSION_GRAVITY);
    }

    public static void setLevelGravityData(Level world, double gravity) {
        Services.PLATFORM.setAttachedValue(world,CommonDataAttachments.DIMENSION_GRAVITY, gravity);
    }

}
