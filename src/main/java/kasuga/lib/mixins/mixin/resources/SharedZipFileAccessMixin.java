package kasuga.lib.mixins.mixin.resources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.zip.ZipFile;

/**
 * Exposes {@code FilePackResources.SharedZipFileAccess#getOrCreateZipFile()}, which is
 * package-private. In 1.19.2 this method lived directly on {@code FilePackResources}
 * (see {@link FilePackResourceMixin} for the accessor that reaches this helper).
 */
@Mixin(targets = "net.minecraft.server.packs.FilePackResources$SharedZipFileAccess")
public interface SharedZipFileAccessMixin {

    @Invoker("getOrCreateZipFile")
    ZipFile invokeGetOrCreateZipFile();
}
