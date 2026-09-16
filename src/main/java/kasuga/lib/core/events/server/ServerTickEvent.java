package kasuga.lib.core.events.server;

import kasuga.lib.KasugaLib;

public class ServerTickEvent {
    public static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post serverTickEvent){
        KasugaLib.STACKS.JAVASCRIPT.GROUP_SERVER.dispatchTick();
    }
}
