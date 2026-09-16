package kasuga.lib.core.client.frontend.gui.canvas;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;

import java.util.function.BiConsumer;

public class DrawTool {
    public static void drawLine(float x1, float y1, float x2, float y2, float lineWidth, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;

        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if(length == 0)
            return;

        float kX = deltaY / length;
        float kY = -deltaX / length;

        float rightUpX = x1 + kX * lineWidth;
        float rightUpY = y1 + kY * lineWidth;
        float rightDownX = x2 + kX * lineWidth;
        float rightDownY = y2 + kY * lineWidth;
        float leftDownX = x1 - kX * lineWidth;
        float leftDownY = y1 - kY * lineWidth;
        float leftUpX = x2 - kX * lineWidth;
        float leftUpY = y2 - kY * lineWidth;

        drawTetragon(rightUpX, rightUpY, rightDownX, rightDownY, leftDownX, leftDownY, leftUpX, leftUpY, vertexProperty);
    }

    public static void drawRect(float x, float y, float width, float height, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        drawTetragon(x, y, x + width, y, x + width, y + height, x, y + height, vertexProperty);
    }

    public static void drawTetragon(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        // 1.21: the buffer is opened by Tesselator.begin(...); BufferBuilder.begin(...) is gone.
        // The vertexProperty lambdas only write a colour, so the format must carry POSITION+COLOR
        // (1.21 throws if any element of the format is left unwritten).
        BufferBuilder vertexConsumer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        vertexConsumer.addVertex(x1, y1, 0);
        vertexProperty.accept(vertexConsumer,0);

        vertexConsumer.addVertex(x2, y2, 0);
        vertexProperty.accept(vertexConsumer,1);

        vertexConsumer.addVertex(x3, y3, 0);
        vertexProperty.accept(vertexConsumer,2);

        vertexConsumer.addVertex(x4, y4, 0);
        vertexProperty.accept(vertexConsumer,3);

        BufferUploader.drawWithShader(vertexConsumer.buildOrThrow());
        RenderSystem.disableBlend();

    }
}
