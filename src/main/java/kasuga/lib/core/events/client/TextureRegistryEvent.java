package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TextureRegistryEvent {

    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterAdditional bakingCompleted) {
        KasugaLib.STACKS.fireTextureRegistry();
        KasugaLib.STACKS.fontRegistry().onRegister();
    }
}
