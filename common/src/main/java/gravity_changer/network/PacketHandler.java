package gravity_changer.network;

import gravity_changer.GravityChanger;
import gravity_changer.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {


    }

    public static ResourceLocation packet(Class<?> clazz) {
        return GravityChanger.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
