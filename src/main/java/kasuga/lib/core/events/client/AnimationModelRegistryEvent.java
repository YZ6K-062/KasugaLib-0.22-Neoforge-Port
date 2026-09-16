package kasuga.lib.core.events.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.ModelPreloadManager;
import kasuga.lib.core.client.model.anim_instance.AnimCacheManager;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.resource.Resources;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.io.IOException;

public class AnimationModelRegistryEvent {

    @SubscribeEvent
    public static void registerAnimations(ModelEvent.RegisterAdditional event) {
        AnimationFile.filesLoaded = true;
        ModelPreloadManager.INSTANCE.scan();
        ModelPreloadManager.INSTANCE.applyAnimPreload();
        for (ResourceLocation location : AnimationFile.UNREGISTERED) {
            try {
                Resource resource = Resources.getResource(location);
                JsonObject object = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
                AnimationFile file = new AnimationFile(location, object);
                AnimationFile.FILES.put(location, file);
            } catch (IOException e) {
                KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location, e);
            }
        }
        AnimCacheManager.INSTANCE.scanFolder();
        // NeoForge 1.21: side-loaded models must be registered with the 'standalone' variant.
        // Any other variant (e.g. "inventory") is rejected by ModelEvent.RegisterAdditional.
        event.register(new ModelResourceLocation(BedrockModelLoader.MISSING_MODEL_LOCATION, "standalone"));
    }
}
