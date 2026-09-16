package kasuga.lib.core.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Shims the removed Forge/NeoForge {@code net.neoforged.fml.DistExecutor}.
 * Kept under kasuga.lib so all existing {@code DistExecutor.unsafeRunWhenOn} /
 * {@code unsafeCallWhenOn} call sites migrate with a single import change.
 */
public final class DistExecutor {

    private DistExecutor() {}

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (dist == FMLEnvironment.dist) {
            toRun.get().run();
        }
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Callable<T>> toCall) {
        if (dist == FMLEnvironment.dist) {
            try {
                return toCall.get().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
