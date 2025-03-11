package gravitychanger;

import gravitychanger.api.RotationParameters;
import gravitychanger.config.GravityChangerConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class GravityChanger {

    public static final String MOD_NAME = "GravityChanger";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "gravitychanger";
    public static final ResourceLocation DATA_COMPONENT_ID =
        id("gravity_data");
    public static final ResourceLocation DIMENSION_DATA_ID =
        id("dimension_data");
    public static ConfigHolder<GravityChangerConfig> configHolder;
    public static GravityChangerConfig config;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        GravityChanger.configHolder = AutoConfig.getConfigHolder(GravityChangerConfig.class);
        GravityChanger.configHolder.registerSaveListener((configHolder, gravityChangerConfig) -> {
            RotationParameters.updateDefault();
            return InteractionResult.PASS;
        });
        GravityChanger.config = GravityChanger.configHolder.getConfig();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }
}