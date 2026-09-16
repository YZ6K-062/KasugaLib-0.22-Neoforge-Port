package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.common.CommandReg;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
public class CommandEvent {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        CommandReg.register(event.getDispatcher());
    }
}