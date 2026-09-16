package kasuga.lib.mixins.mixin.client;

import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21 split the old {@code ModelBakery} class into the {@code ModelBaker} interface plus the
 * {@code ModelBakery} implementation class, so a class mixin has to target {@code ModelBakery}.
 * Targeting {@code ModelBaker} made Mixin abort with
 * "@Mixin target type mismatch: ... is an interface".
 * <p>
 * Note: the only injection this mixin ever had is commented out upstream, so it is intentionally
 * empty here as well.
 */
@Mixin(ModelBakery.class)
public class MixinModelBakery {

//    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
//    private boolean addAll(Set<Material> instance, Collection<Material> es) {
//        return instance.addAll(es) & instance.addAll(BedrockModelLoader.ADDITIONAL_MATERIALS);
//    }
}
