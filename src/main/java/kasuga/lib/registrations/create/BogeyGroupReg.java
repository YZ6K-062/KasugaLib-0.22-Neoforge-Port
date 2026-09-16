package kasuga.lib.registrations.create;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisualizer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Create 6.0 重写后 {@link BogeyStyle.Builder#size} 改为同时接收 block 和 renderer。
 * 这里把 (size, renderer, BogeyBlockReg) 三元组收集起来，在 submit 时构造
 * {@code Supplier<Supplier<BogeyStyle.SizeRenderer>>}：外层 supplier 每次 .get() 返回新的
 * 内层 supplier，内层 supplier 每次 .get() 返回一个全新的 SizeRenderer，
 * 这样 {@code Builder.size} 每次被调时都能拿到一份新的 SizeRenderer（Flywheel 只在
 * 客户端注册 sizeRenderers，所以这里多包一层是安全的）。
 *
 * <p>Create 6.0 的 {@link BogeyRenderer} 是接口（{@code void render(...MultiBufferSource...)}），
 * 而 {@link BogeyVisualizer} 是 {@code @FunctionalInterface}（{@code createVisual(ctx, pt, ic) -> BogeyVisual}）。
 * 提供 visualizer 是 bogey 在 Flywheel instancing 渲染管线下的必备配套；
 * 当 Kuayue 那种旧式 renderer 只在 {@code render(...)} 里直接画 PoseStack 时，可以不写
 * visualizer，这边用 {@link #createNoopVisualizer()} 兜底（不参与 Flywheel instancing，
 * 但 bogey 仍能用 Renderer 的直接渲染路径）。后续 Kuayue 给每个 renderer 配套一个
 * visualizer 时，调用 {@link #bogey(BogeySizes.BogeySize, Supplier, Supplier, BogeyBlockReg)}
 * 传入即可。
 */
public class BogeyGroupReg extends Reg {
    private final List<BogeyContext> contexts;
    private Supplier<BogeyRenderer> defaultRenderer;
    private Supplier<BogeyVisualizer> defaultVisualizer;
    private String cycleGroup = "";
    private Component translationName;
    private ParticleOptions contactParticle;
    private ParticleOptions smokeParticle;
    private BogeyStyle style = null;

    public BogeyGroupReg(String registrationKey, String cycleGroup) {
        super(registrationKey);
        this.cycleGroup = cycleGroup;
        this.contexts = new ArrayList<>();
    }

    public BogeyGroupReg translationKey(String key) {
        this.translationName = Component.translatable(key);
        return this;
    }

    public BogeyGroupReg cycleGroup(String group) {
        this.cycleGroup = group;
        return this;
    }

    public BogeyGroupReg defaultRenderer(Supplier<BogeyRenderer> renderer) {
        this.defaultRenderer = renderer;
        return this;
    }

    public BogeyGroupReg defaultVisualizer(Supplier<BogeyVisualizer> visualizer) {
        this.defaultVisualizer = visualizer;
        return this;
    }

    public BogeyGroupReg contactParticle(ParticleOptions particle) {
        this.contactParticle = particle;
        return this;
    }

    public BogeyGroupReg smokeParticle(ParticleOptions particle) {
        this.smokeParticle = particle;
        return this;
    }

    // ---- bogey() overloads ----

    public BogeyGroupReg bogeyWithDefaultRenderer(BogeySizes.BogeySize size, BogeyBlockReg<?> reg) {
        if (defaultRenderer == null)
            throw new IllegalStateException("No default renderer has been set for BogeyGroupReg " + registrationKey);
        return bogey(size, defaultRenderer, reg);
    }

    public BogeyGroupReg bogey(BogeySizes.BogeySize size, Supplier<BogeyRenderer> renderer, BogeyBlockReg<?> reg) {
        contexts.add(new BogeyContext(size, renderer, defaultVisualizer, reg));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizes.BogeySize size, Supplier<BogeyRenderer> renderer,
                               Supplier<BogeyVisualizer> visualizer, BogeyBlockReg<?> reg) {
        contexts.add(new BogeyContext(size, renderer, visualizer, reg));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizeReg size, Supplier<BogeyRenderer> renderer, BogeyBlockReg<?> reg) {
        return bogey(size.getSize(), renderer, reg);
    }

    public BogeyGroupReg bogey(BogeySizeReg size, Supplier<BogeyRenderer> renderer,
                               Supplier<BogeyVisualizer> visualizer, BogeyBlockReg<?> reg) {
        return bogey(size.getSize(), renderer, visualizer, reg);
    }

    // ---- submit ----

    @Override
    public BogeyGroupReg submit(SimpleRegistry registry) {
        BogeyStyle.Builder builder = new BogeyStyle.Builder(
                registry.asResource(registrationKey),
                registry.asResource(cycleGroup));
        if (translationName != null) builder.displayName(translationName);
        if (contactParticle != null) builder.contactParticle(contactParticle);
        if (smokeParticle != null) builder.smokeParticle(smokeParticle);

        for (BogeyContext ctx : contexts) {
            // 构造 (Supplier<AbstractBogeyBlock<?>>, Supplier<Supplier<? extends SizeRenderer>>)
            Supplier<? extends AbstractBogeyBlock<?>> blockSupplier = ctx.blockReg().getEntry()::get;
            Supplier<? extends BogeyVisualizer> vis = ctx.visualizer() != null ? ctx.visualizer() : BogeyGroupReg::createNoopVisualizer;
            Supplier<Supplier<? extends BogeyStyle.SizeRenderer>> sizeRendererSupplier =
                    () -> () -> new BogeyStyle.SizeRenderer(ctx.renderer().get(), vis.get());
            builder.size(ctx.size(), blockSupplier, sizeRendererSupplier);
        }
        style = builder.build();
        return this;
    }

    public BogeyStyle getStyle() {
        return style;
    }

    @Override
    public String getIdentifier() {
        return "bogey_group";
    }

    /**
     * 当 bogey 的 renderer 不参与 Flywheel instancing（直接在 render() 里画 PoseStack）时，
     * 这里提供一个 stub {@link BogeyVisualizer}：返回的 BogeyVisual 所有方法都 noop，
     * Flywheel 会照样实例化它但不画东西，不影响 Renderer 的直接 PoseStack 渲染。
     */
    public static BogeyVisualizer createNoopVisualizer() {
        return new BogeyVisualizer() {
            @Override
            public BogeyVisual createVisual(VisualizationContext ctx, float partialTick, boolean inContraption) {
                return new BogeyVisual() {
                    @Override public void update(CompoundTag bogeyData, float wheelAngle, PoseStack poseStack) {}
                    @Override public void hide() {}
                    @Override public void updateLight(int packedLight) {}
                    @Override public void collectCrumblingInstances(Consumer<Instance> consumer) {}
                    @Override public void delete() {}
                };
            }
        };
    }

    record BogeyContext(BogeySizes.BogeySize size,
                        Supplier<BogeyRenderer> renderer,
                        Supplier<BogeyVisualizer> visualizer,
                        BogeyBlockReg<?> blockReg) {}
}
