package kasuga.lib.core.client.frontend.gui.canvas.glfw;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import kasuga.lib.core.client.frontend.gui.canvas.CanvasManager;
import kasuga.lib.core.util.Callback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.client.Minecraft.ON_OSX;

public class CanvasRenderer {
    private final CanvasManager manager;
    RenderBuffers renderBuffer = Minecraft.getInstance().renderBuffers();

    TextureTarget target;

    Queue<Callback> taskQueue = new ArrayDeque<>();


    // Was never initialised, so render() threw a NPE on the very first frame. Default to clearing every
    // frame: the canvas is fully redrawn on each pass, so leaving the previous frame in the target would
    // smear it. Kept non-final so callers can opt out of clearing.
    private AtomicBoolean shouldClear = new AtomicBoolean(true);

    public CanvasRenderer(CanvasManager manager, int width, int height){
        target = new TextureTarget(256,256,true, ON_OSX);
        this.manager = manager;
        init();
    }

    public void init(){
        target.createBuffers(target.width, target.height, false);
        target.setClearColor(0,1,0,1);
        target.clear(true);
    }
    public void render(){
        if(this.shouldClear.get())
            target.clear(true);

        RenderSystem.clear(16640, ON_OSX);
        target.bindWrite(true);
        // 1.21: RenderSystem.enableTexture()/disableTexture() were removed with the new
        // texture/render-pipeline handling, so those calls are simply dropped.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1.21: JOML exposes the orthographic builders as ortho()/setOrtho().
        Matrix4f matrix4f = new Matrix4f().ortho(0.0F, 256F, 0.0F, 256F, 1000.0F, 3000.0F);
        // 1.21: setProjectionMatrix now requires the vertex sorting strategy.
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        // 1.21: the model-view stack is a JOML Matrix4fStack, not a PoseStack.
        Matrix4fStack poseStack = RenderSystem.getModelViewStack();
        poseStack.clear();
        poseStack.translate(0.0F, 0.0F, 1000.0F - 3000.0F);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        actualRender();
        target.unbindWrite();
        RenderSystem.disableBlend();
        RenderSystem.clear(16640, ON_OSX);
    }

    public void renderToBuffer(MultiBufferSource.BufferSource bufferSource, int x, int y, int width, int height){
        VertexConsumer consumer = bufferSource.getBuffer(CanvasRenderType.CANVAS.apply(target.getColorTextureId()));
        // 1.21: vertex()/uv()/endVertex() were replaced by addVertex()/setUv(); the vertex is
        // finished implicitly by the next addVertex() (or by build()).
        consumer.addVertex(x, y + height, 0).setUv(0, 1);
        consumer.addVertex(x + width, y + height, 0).setUv(1, 1);
        consumer.addVertex(x + width, y, 0).setUv(1, 0);
        consumer.addVertex(x, y, 0).setUv(0, 0);
        bufferSource.endBatch();
    }


    public void renderToScreen(float x,float y,float width,float height){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, target.getColorTextureId());
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.enableBlend();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(x, y, 0f).setUv(0, 1);
        buffer.addVertex(x, y + height, 0f).setUv(0, 0);
        buffer.addVertex(x + width, y + height, 0f).setUv(1, 0);
        buffer.addVertex(x + width, y, 0f).setUv(1, 1);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private void actualRender() {
        MultiBufferSource.BufferSource bufferSource = renderBuffer.bufferSource();
        while(!taskQueue.isEmpty()){
            taskQueue.poll().execute();
        }
        bufferSource.endBatch();
    }

    public void close(){
        target.destroyBuffers();
        this.manager.remove(this);
    }

    public void pushTask(Callback drawFunction) {
        taskQueue.add(drawFunction);
    }
}
