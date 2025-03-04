package gravity_changer.util;

import gravity_changer.api.RotationParameters;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GravityDirEffect(
        @NotNull Direction direction,
        @Nullable RotationParameters rotationParameters,
        double priority
) {

}
