package kasuga.lib.core.client.frontend.gui.canvas.glfw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Function;

public class CanvasRenderType {
    // 1.21 removed the "new_entity" core shader. The canvas quad only ever carries
    // position + uv, so it is created as a plain textured quad (POSITION_TEX +
    // position_tex shader). This keeps the vertex data complete, which 1.21's
    // BufferBuilder now enforces.
    protected static final RenderStateShard.ShaderStateShard POSITION_TEX_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader);
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });

    protected static final Function<Integer, RenderType> CANVAS = Util.memoize((target) -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_TEX_SHADER)
                .setTextureState(new CanvasTextureState(target))
                .setTransparencyState(NO_TRANSPARENCY)
                .createCompositeState(true);
        return RenderType.create(
                "canvas",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                state
        );
    });
}
