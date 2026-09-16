package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.21 moved the zip handle out of {@code FilePackResources} into its nested helper
 * {@code FilePackResources.SharedZipFileAccess}, so the old
 * {@code @Invoker getOrCreateZipFile} no longer resolves on {@code FilePackResources} itself.
 * This accessor exposes the helper, and {@link SharedZipFileAccessMixin} exposes the zip file.
 */
@Mixin(FilePackResources.class)
public interface FilePackResourceMixin {

    @Accessor("zipFileAccess")
    FilePackResources.SharedZipFileAccess getZipFileAccess();
}
