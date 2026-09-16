package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class RenderTickEvent {
    @SubscribeEvent
    public static void onRenderTick(RenderFrameEvent.Post renderTickEvent){
        // KasugaLib.STACKS.JAVASCRIPT.renderTick();
        // KasugaLib.STACKS.RENDER.ifPresent(GuiEngine::renderTick);
    }
}
