package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.client.ModelPathRemapper;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Redirects model id lookup so that KasugaLib's registered model mappings are honoured.
 * <p>
 * The 1.19.2 version intercepted {@code new ResourceLocation(namespace, path)} inside
 * {@code ModelBakery#loadModel}. That call site no longer exists: in 1.21
 * {@code ModelBakery#loadBlockModel} receives the model id and converts it to a file with
 * {@code MODEL_LISTER.idToFile(id)}. Intercepting that conversion is the equivalent hook — the
 * original decision logic is preserved verbatim in {@link ModelPathRemapper}.
 * <p>
 * Note the target must be {@code ModelBakery}, not {@code ModelBaker}: in 1.21 {@code ModelBaker}
 * is an interface and Mixin aborts at PREPARE when a class mixin targets one.
 */
@Mixin(ModelBakery.class)
public abstract class MixinBlockModelBinding {

    @Redirect(
            method = "loadBlockModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;idToFile(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation kasugalib$remapModelFile(FileToIdConverter converter, ResourceLocation id) {
        return converter.idToFile(ModelPathRemapper.remapModelId(id));
    }
}
