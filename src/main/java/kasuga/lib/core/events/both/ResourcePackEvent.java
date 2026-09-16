package kasuga.lib.core.events.both;

import kasuga.lib.core.resource.CustomResourceReloadListener;
import net.minecraft.server.commands.DataPackCommand;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ResourcePackEvent {

    @SubscribeEvent
    public static void onResourcePackReload(AddReloadListenerEvent reloadListenerEvent){
        // reloadListenerEvent.addListener(CustomResourceReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientResourcePackReload(RegisterClientReloadListenersEvent event) {
        // event.registerReloadListener(CustomResourceReloadListener.INSTANCE);
    }
}
