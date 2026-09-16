package kasuga.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.frontend.commands.FrontendCommands;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.webserver.GuiWebServerEndpoint;
import kasuga.lib.core.javascript.commands.JavascriptModuleCommands;
import kasuga.lib.core.events.both.CommandEvent;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.util.Envs;
import kasuga.lib.core.resource.KasugaPackResource;
import kasuga.lib.core.webserver.KasugaHttpServer;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.mixins.mixin.MultiPackResourceManagerAccessor;
import kasuga.lib.mixins.mixin.ReloadableResourceManagerAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import kasuga.lib.core.util.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(KasugaLib.MOD_ID)
public class KasugaLib {
    public static final String MOD_ID = "kasuga_lib";
    public static final Logger MAIN_LOGGER = createLogger("MAIN");
    // NeoForge 1.21.1 still exposes the mod event bus this way (deprecated but functional),
    // keeping the static-init pattern used across the codebase intact.
    public static IEventBus EVENTS;

    public static KasugaLibStacks STACKS;
    public static ModContainer CONTAINER;
    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    @Nullable
    @Getter
    public static MinecraftServer server = null;

    public KasugaLib(IEventBus modEventBus, ModContainer modContainer) {
        EVENTS = modEventBus;
        CONTAINER = modContainer;
        STACKS = new KasugaLibStacks(EVENTS);
        // 1.21 strictness: an empty @SubscribeEvent-less class can no longer be
        // handed to EVENT_BUS.register(this). All real listeners are attached by
        // KasugaLibStacks via bus.addListener(...), so the self-register is gone.
        NeoForge.EVENT_BUS.register(CommandEvent.class);
        AllPackets.init();
        // YogaExample.example();
        JavascriptModuleCommands.invoke();
        FrontendCommands.invoke();
        KasugaLibConfig.invoke();
        KasugaHttpServer.invoke();
        if (Envs.isDevEnvironment())
            AllExampleElements.invoke();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> LayoutEngines::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> GuiWebServerEndpoint::invoke);
    }

    public static Logger createLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}