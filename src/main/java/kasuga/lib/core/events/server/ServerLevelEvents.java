package kasuga.lib.core.events.server;


import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ServerLevelEvents {

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {}

    @SubscribeEvent
    public void onLevelSave(LevelEvent.Save event) {}

    @SubscribeEvent
    public void onLevelExit(LevelEvent.Unload event) {}
}
