package kasuga.lib.core.menu.targets;

import kasuga.lib.core.menu.base.GuiBindingTarget;
import net.minecraft.resources.ResourceLocation;

public class Target<U> implements GuiBindingTarget<U> {
    private final ResourceLocation id;

    protected Target(ResourceLocation id) {
        this.id = id;
    }

    public static Target create(ResourceLocation id){
        return new Target(id);
    }

    public static Target<ClientTextureTarget> TEXTURE = new Target<ClientTextureTarget>(ResourceLocation.parse("kasuga_lib:texture"));
    public static Target<ClientScreenTarget> SCREEN = new Target<ClientScreenTarget>(ResourceLocation.parse("kasuga_lib:screen"));
    public static Target<WorldRendererTarget> WORLD_RENDERER = new Target<WorldRendererTarget>(ResourceLocation.parse("kasuga_lib:world_renderer"));

    public static void register(){}
}
