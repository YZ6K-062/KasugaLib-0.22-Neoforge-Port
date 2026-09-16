package kasuga.lib.core.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;

/**
 * Model path redirection for the KasugaLib model mappings.
 * <p>
 * In 1.19.2 this logic lived entirely inside {@code MixinBlockModelBinding}, which intercepted a
 * {@code new ResourceLocation(namespace, path)} inside {@code ModelBakery#loadModel}. 1.21 split
 * model loading ({@code ModelBakery#loadBlockModel} for models,
 * {@code BlockStateModelLoader#loadBlockStateDefinitions} for block states}) and funnels every
 * id-to-file conversion through {@code FileToIdConverter#idToFile}, so the same decision logic is
 * kept here and applied from those two new injection points.
 * <p>
 * The decision logic itself is unchanged from the 1.19.2 original.
 */
@Inner
public final class ModelPathRemapper {

    private ModelPathRemapper() {}

    /**
     * Applied to a model id, e.g. {@code namespace:item/green_apple_item}.
     * The registry stores those mappings under {@code namespace:item/<key>.json}.
     */
    public static ResourceLocation remapModelId(ResourceLocation id) {
        return remap(id.getNamespace(), id.getPath());
    }

    /**
     * Applied to an already-resolved block state file location, e.g.
     * {@code namespace:blockstates/x.json}.
     */
    public static ResourceLocation remapBlockstateFile(ResourceLocation file) {
        return remap(file.getNamespace(), file.getPath());
    }

    private static ResourceLocation remap(String namespace, String path) {
        SimpleRegistry registry = KasugaLib.STACKS.getRegistries().getOrDefault(namespace, null);
        if (registry == null) return ResourceLocation.fromNamespaceAndPath(namespace, path);
        ModelMappings mappings = registry.modelMappings();
        if (!mappings.isMapFinished()) {
            try {
                mappings.map();
            } catch (Exception e) {
                KasugaLib.MAIN_LOGGER.error("Encountered error while mapping Models!", e);
                throw new ReportedException(CrashReport.forThrowable(e, "Encountered error while mapping Models!"));
            }
        }
        if (path.startsWith(ModelMappings.IDENTIFIER))
            return mappings.getMappings(ResourceLocation.fromNamespaceAndPath(namespace, path));
        ResourceLocation s2 = ResourceLocation.fromNamespaceAndPath(namespace, path + ".json");
        if (!mappings.containsMapping(s2)) return ResourceLocation.fromNamespaceAndPath(namespace, path);
        return mappings.getMappings(s2);
    }
}
