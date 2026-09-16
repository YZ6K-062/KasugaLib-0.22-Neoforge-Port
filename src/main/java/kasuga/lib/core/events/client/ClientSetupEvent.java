package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.minecraft.ClientAddon;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.addons.resource.ResourceAdapter;
import kasuga.lib.core.addons.resource.ResourceProvider;
import kasuga.lib.core.client.frontend.commands.MetroModuleLoader;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.util.Start;
import kasuga.lib.core.webserver.KasugaHttpServer;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientSetupEvent {

    /**
     * NeoForge dispatches {@link FMLClientSetupEvent} on a parallel mod-loading executor and this
     * listener is observed to fire more than once for the same mod container. Everything below
     * pushes into process-wide registries (styles, node types, image providers, ...), so a second
     * pass blows up with e.g. "Style already registered". Guard the whole body with a CAS to make
     * client setup genuinely one-shot.
     */
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!INITIALIZED.compareAndSet(false, true)) {
            return;
        }
        Start.printLogo(KasugaLib.MAIN_LOGGER);
        // NOTE: menus are hooked through RegisterMenuScreensEvent (see onRegisterMenuScreens below),
        // which fires while Minecraft boots; this event runs too late for it.
        KasugaLib.STACKS.JAVASCRIPT.setupClient();
        KasugaLib.STACKS.GUI.ifPresent(GuiEngine::init);
        ClientAddon.init();
        MetroModuleLoader.init();
        KasugaHttpServer.onClientStart();
    }

    /**
     * 1.21: vanilla made {@code MenuScreens.register} private, so all registered menus are pushed
     * into the {@link RegisterMenuScreensEvent} instead. That event is fired from
     * {@code MenuScreens.init()} during client boot, therefore this listener must be added from the
     * mod constructor rather than from {@link FMLClientSetupEvent}.
     */
    @OnlyIn(Dist.CLIENT)
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        for (SimpleRegistry registry : KasugaLib.STACKS.getRegistries().values()) {
            registry.getCahcedMenus().forEach((a, b) -> b.hookMenuAndScreen(event));
            registry.getCahcedMenus().clear();
        }
    }
}
