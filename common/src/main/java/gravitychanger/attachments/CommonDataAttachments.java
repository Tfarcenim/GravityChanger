package gravitychanger.attachments;

import com.mojang.serialization.Codec;
import gravitychanger.platform.Services;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommonDataAttachments {

    private static final Map<ResourceLocation,CommonDataAttachment<?>> MAP =new HashMap<>();

    public static final CommonDataAttachment<Double> DIMENSION_GRAVITY =
            register(CommonDataAttachment.create(o -> 1d)
                    .networkSynchronized(ByteBufCodecs.DOUBLE)
                    .codec(Codec.DOUBLE)
                    .build("dimension_gravity"));

    public static CommonDataAttachment<?> lookup(ResourceLocation location) {
        return MAP.get(location);
    }

    static <T> CommonDataAttachment<T> register(CommonDataAttachment<T> type) {
        Services.PLATFORM.registerDataAttachment(type);
        Objects.requireNonNull(type.getAttachment());
        MAP.put(type.name,type);
        return type;
    }

    public static void init() {

    }
}