package gravity_changer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;

public class GravityField {
    public static boolean isOnField(Entity entity) {
        BlockState feedBlockState = entity.getWorld().getBlockState(entity.getSteppingPos());
        return feedBlockState.getBlock() == Blocks.GLOWSTONE;
    }
    
    public static void init() {
//        GravityComponent.GRAVITY_DIR_MODIFIER_EVENT.register(new GravityComponent.GravityDirModifierCallback() {
//            @Override
//            public Direction transform(GravityComponent component, Direction direction) {
//                if (isOnField(component.entity)) {
//                    return Direction.NORTH;
//                }
//
//                return direction;
//            }
//        });
    }
}
