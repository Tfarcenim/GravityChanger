package gravitychanger;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;

import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;

public record DirectionFamily<T>(EnumMap<Direction, T> map) {
    public static final Direction[] DIRECTIONS = Direction.values();

    public static <T> DirectionFamily<T> createAndRegister(Registry<? super T> registry, Function<Direction,T> creator,String prefix, String suffix) {
        EnumMap<Direction,T> m = new EnumMap<>(Direction.class);
        for (Direction direction : DIRECTIONS) {
            m.put(direction, Registry.register(registry,GravityChanger.id(prefix+"_"+direction.getName()+"_"+ suffix),creator.apply(direction)));
        }
        return new DirectionFamily<>(m);
    }

    public void forEach(Consumer<T> consumer) {
        map.forEach((direction, supplier) -> consumer.accept(supplier));
    }

    public T getEntry(Direction direction) {
        return map.get(direction);
    }
}
