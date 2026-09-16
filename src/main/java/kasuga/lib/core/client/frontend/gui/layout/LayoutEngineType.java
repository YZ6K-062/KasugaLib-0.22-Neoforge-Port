package kasuga.lib.core.client.frontend.gui.layout;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;

public class LayoutEngineType<T extends LayoutEngine> {
    private final Lazy<T> factory;

    public LayoutEngineType(Supplier<Supplier<T>> factory) {
        this.factory = Lazy.of(()->factory.get().get());
    }

    public T create() {
        return factory.get();
    }

    public static <T extends LayoutEngine> LayoutEngineType<T> of(Supplier<Supplier<T>> factory) {
        return new LayoutEngineType<>(factory);
    }
}
