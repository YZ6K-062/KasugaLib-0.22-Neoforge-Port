package kasuga.lib.example_env.block.green_apple;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.client.render.RendererUtil;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import kasuga.lib.core.util.DistExecutor;

public class GreenAppleTile extends BlockEntity {

    public float sec = 0f;
    public boolean direction = false;
    public boolean saved = false;
    public GreenAppleTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public GreenAppleTile(BlockPos pos, BlockState state) {
        this(AllExampleElements.greenAppleTile.getType(), pos, state);
    }

    // 1.21: BlockEntity no longer exposes getRenderBoundingBox(); the renderer does
    // (see GreenAppleTileRenderer#getRenderBoundingBox).

    @Override
    public void onChunkUnloaded() {}
}
