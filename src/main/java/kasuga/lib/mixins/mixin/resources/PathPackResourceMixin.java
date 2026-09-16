package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.PathPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(value = PathPackResources.class, remap = false)
public interface PathPackResourceMixin {
    @Accessor("root")
    public Path getRoot();
}
