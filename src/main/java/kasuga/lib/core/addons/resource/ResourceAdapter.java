package kasuga.lib.core.addons.resource;

import kasuga.lib.mixins.mixin.resources.FilePackResourceMixin;
import kasuga.lib.mixins.mixin.resources.PathPackResourceMixin;
import kasuga.lib.mixins.mixin.resources.SharedZipFileAccessMixin;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.VanillaPackResources;
import net.neoforged.neoforge.resource.EmptyPackResources;

import java.util.ArrayList;
import java.util.List;

public class ResourceAdapter {
    public static List<PackResources> flatten(List<PackResources> resources){
        return new ArrayList<>(resources);
    }

    public static List<ResourceProvider> transform(List<PackResources> resources) {
        List<ResourceProvider> result = new ArrayList<>();
        for (PackResources resource : resources) {
            if(resource instanceof FilePackResources file){
                // 1.21: the zip handle lives on FilePackResources.SharedZipFileAccess.
                FilePackResources.SharedZipFileAccess zip =
                        ((FilePackResourceMixin) file).getZipFileAccess();
                result.add(new VanillaFileResourcePackProvider(
                        ((SharedZipFileAccessMixin) zip).invokeGetOrCreateZipFile()));
            }else if(resource instanceof PathPackResources path){
                result.add(new VanillaPathResourcePackProvider(((PathPackResourceMixin) path).getRoot(), path));
            }else if(resource instanceof VanillaPackResources){
                continue;
            }else if(resource instanceof EmptyPackResources){
                // 1.21: NeoForge introduces EmptyPackResources as a placeholder for
                // internal packs (e.g. the "mod_resources" stub). It owns no resources
                // so we just skip it instead of failing.
                continue;
            }else{
                // Unknown pack implementations (NeoForge keeps adding internals such as
                // EmptyPackResources) must never abort client setup — skip and warn instead.
                System.err.println("[KasugaLib] Skipping unknown asset pack: "
                        + resource.packId() + " - " + resource.getClass().getName());
            }
        }
        return result;
    }

    public static List<ResourceProvider> adapt(List<PackResources> resources){
        for (PackResources packResources : flatten(resources)) {
            System.out.println("Asset pack: " + packResources.packId() + " - " + packResources.getClass().getName());
        }
        return transform(flatten(resources));
    }
}
