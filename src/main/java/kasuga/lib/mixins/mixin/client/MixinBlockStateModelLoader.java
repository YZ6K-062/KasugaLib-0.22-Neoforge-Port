package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.client.ModelPathRemapper;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Block state half of the KasugaLib model path redirection.
 * <p>
 * In 1.19.2 the {@code blockstates/...} branch was handled by the same
 * {@code ModelBakery#loadModel} redirect as the models. 1.21 moved block state loading into
 * {@code BlockStateModelLoader}, which resolves the block state id to its file with
 * {@code BLOCKSTATE_LISTER.idToFile(id)}. Intercepting that conversion keeps
 * {@code ModelMappings#map()}'s {@code blockstates/x.json -> blockstates/<dir>/x.json} entries
 * working, because {@code blockStateResources} is built from the full (nested) file listing.
 */
@Mixin(BlockStateModelLoader.class)
public abstract class MixinBlockStateModelLoader {

    @Redirect(
            method = "loadBlockStateDefinitions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;idToFile(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation kasugalib$remapBlockstateFile(FileToIdConverter converter, ResourceLocation id) {
        return ModelPathRemapper.remapBlockstateFile(converter.idToFile(id));
    }
}
