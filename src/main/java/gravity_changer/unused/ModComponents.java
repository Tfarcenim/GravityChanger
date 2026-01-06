package gravity_changer.unused;

import gravity_changer.GravityChangerMod;
//import net.minecraft.component.DataComponentType;


public class ModComponents {
    protected static void initialize() {
        //To notify console that the components successfully registered.
        GravityChangerMod.LOGGER.info("Registering {} components", GravityChangerMod.NAMESPACE);
    }

    /*public static <T> DataComponentType<T> register(String path, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("tutorial", path), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static final DataComponentType<?> SIDEDATA = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(GravityChangerMod.NAMESPACE, "side_data"),
            DataComponentType.<GravityPlatingBlockEntity.SideData>builder().codec(null).build()
    );*/
}
