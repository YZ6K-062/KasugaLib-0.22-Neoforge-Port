package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.block_bench_model.anim.instance.AnimationController;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.client.model.anim_instance.AnimateTickerManager;
import kasuga.lib.registrations.client.KeyBindingReg;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientTickEvent {
    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
        if(KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT != null){
            KasugaLib.STACKS.JAVASCRIPT.GROUP_CLIENT.dispatchTick();
        }

        KeyBindingReg.onClientTick();
        AnimateTickerManager.INSTANCE.tickGui();

        KasugaLib.STACKS.MENU.clientTick();
        AnimationController.CONTROLLERS.forEach(
                AnimationController::tick
        );

        KasugaLib.STACKS.GUI.ifPresent(GuiEngine::renderTick);
        // deal with world ticker;
    }

    @SubscribeEvent
    public static void onGuiTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
        // deal with gui ticker;
        AnimateTickerManager.INSTANCE.tickWorld();
    }
}
