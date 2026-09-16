package kasuga.lib.core.util.data_type;

/**
 * Minimal ordered pair, mirroring the shape of Create's
 * {@code net.createmod.catnip.data.Couple} that the 0.5.1 port relied on.
 * Used by the track-graph simplifier / walker.
 */
public class Couple<T> {

    private final T first;
    private final T second;

    public Couple(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public static <T> Couple<T> create(T first, T second) {
        return new Couple<>(first, second);
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }
}
