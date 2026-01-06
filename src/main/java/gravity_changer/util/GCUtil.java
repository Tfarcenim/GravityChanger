package gravity_changer.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class GCUtil {
    public static MutableText getLinkText(String link) {
        return Text.literal(link).styled(
            style -> style.withClickEvent(new ClickEvent(
                ClickEvent.Action.OPEN_URL, link
            )).withUnderline(true)
        );
    }
    
    public static MutableText getDirectionText(Direction gravityDirection) {
        return Text.translatable("direction." + gravityDirection.getName());
    }
    
    public static double distanceToRange(double value, double rangeStart, double rangeEnd) {
        if (value < rangeStart) {
            return rangeStart - value;
        }
        
        if (value > rangeEnd) {
            return value - rangeEnd;
        }
        
        return 0;
    }
    
    public static boolean isClientPlayer(Entity entity) {
        if (entity.getWorld().isClient()) {
            return entity instanceof ClientPlayerEntity;
        }
        return false;
    }
    
    public static boolean isRemotePlayer(Entity entity) {
        if (entity.getWorld().isClient()) {
            return entity instanceof OtherClientPlayerEntity;
        }
        return false;
    }
}
