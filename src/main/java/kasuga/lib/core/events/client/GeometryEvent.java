package kasuga.lib.core.events.client;

import kasuga.lib.core.client.block_bench_model.BlockBenchModelLoader;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.BedrockModelLoader;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class GeometryEvent {

    @SubscribeEvent
    public static void registerGeometry(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath("kasuga_lib", "bedrock_model"), BedrockModelLoader.INSTANCE);
        event.register(ResourceLocation.fromNamespaceAndPath("kasuga_lib", "bedrock_animated"), AnimModelLoader.INSTANCE);
        event.register(ResourceLocation.fromNamespaceAndPath("kasuga_lib", "blockbench_model"), BlockBenchModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BedrockModelLoader.INSTANCE);
        event.registerReloadListener(AnimModelLoader.INSTANCE);
        event.registerReloadListener(BlockBenchModelLoader.INSTANCE);
    }
}
