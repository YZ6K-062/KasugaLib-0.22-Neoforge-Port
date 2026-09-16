package kasuga.lib.core.client.frontend.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.*;

public class VertexBufferCache {
    HashMap<RenderType, VertexBuffer> buffers;
    MultiBufferStore multiBufferStore;
    public VertexBufferCache() {
        this.buffers = new HashMap<>();
        this.multiBufferStore = new MultiBufferStore(buffers);
    }

    public MultiBufferStore getMultiBufferStore() {
        return multiBufferStore;
    }

    public static class MultiBufferStore implements MultiBufferSource {
        HashMap<RenderType, VertexBuffer> buffers;

        public MultiBufferStore(HashMap<RenderType, VertexBuffer> buffers) {
            this.buffers = buffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderType pRenderType) {
            return buffers.computeIfAbsent(pRenderType, (type) -> new VertexBuffer(type));
        }

        public void upload(Matrix4f pose, MultiBufferSource original){
            for (RenderType type : buffers.keySet()) {
                VertexBuffer buffer = buffers.get(type);
                if (buffer != null) {
                    buffer.apply(pose, original.getBuffer(type));
                }
            }
        }

        public void upload(Matrix4f pose) {
            for (Map.Entry<RenderType, VertexBuffer> entry : buffers.entrySet()) {
                VertexBuffer buffer = buffers.get(entry.getKey());
                buffer.apply(pose, entry.getKey());
            }
        }

        public void begin(){
            for (RenderType type : buffers.keySet()) {
                VertexBuffer buffer = buffers.get(type);
                if (buffer != null) {
                    buffer.reset();
                }
            }
        }

        public void gc(){
            Iterator<Map.Entry<RenderType, VertexBuffer>> iterator = buffers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RenderType, VertexBuffer> entry = iterator.next();
                VertexBuffer buffer = entry.getValue();
                if (buffer.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public static class VertexBuffer implements VertexConsumer {
        final VertexFormat format;
        final List<Vertex> vertices = new ArrayList<>();

        float x, y, z;
        float u, v;
        int r, g, b, a;
        float nx, ny, nz;

        VertexBuffer(RenderType renderType) {
            this.format = renderType.format();
        }

        public void reset(){
            vertices.clear();
        }

        public boolean isEmpty(){
            return vertices.isEmpty();
        }

        @Override
        public VertexConsumer addVertex(float pX, float pY, float pZ) {
            vertices.add(new Vertex(pX, pY, pZ, u, v, r, g, b, a, nx, ny, nz));
            return this;
        }

        @Override
        public VertexConsumer setColor(int pR, int pG, int pB, int pA) {
            this.r = pR; this.g = pG; this.b = pB; this.a = pA;
            return this;
        }

        @Override
        public VertexConsumer setUv(float pU, float pV) {
            this.u = pU; this.v = pV;
            return this;
        }

        @Override
        public VertexConsumer setUv1(int pU, int pV) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int pU, int pV) {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float pX, float pY, float pZ) {
            this.nx = pX; this.ny = pY; this.nz = pZ;
            return this;
        }

        public void apply(Matrix4f matrix4f, VertexConsumer consumer){
            for (Vertex vertex : vertices) {
                consumer.setColor(vertex.r, vertex.g, vertex.b, vertex.a)
                        .setUv(vertex.u, vertex.v)
                        .setNormal(vertex.nx, vertex.ny, vertex.nz)
                        .addVertex(matrix4f, vertex.x, vertex.y, vertex.z);
            }
        }

        public void apply(Matrix4f matrix4f, RenderType type){
            RenderSystem.assertOnRenderThread();
            BufferBuilder buffer = Tesselator.getInstance().begin(type.mode(), type.format());
            apply(matrix4f, buffer);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        static final class Vertex {
            final float x, y, z, u, v, nx, ny, nz;
            final int r, g, b, a;
            Vertex(float x, float y, float z, float u, float v, int r, int g, int b, int a, float nx, float ny, float nz) {
                this.x = x; this.y = y; this.z = z;
                this.u = u; this.v = v;
                this.r = r; this.g = g; this.b = b; this.a = a;
                this.nx = nx; this.ny = ny; this.nz = nz;
            }
        }
    }
}
