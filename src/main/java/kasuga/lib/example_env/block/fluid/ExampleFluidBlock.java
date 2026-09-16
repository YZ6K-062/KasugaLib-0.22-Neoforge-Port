package kasuga.lib.example_env.block.fluid;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ExampleFluidBlock extends LiquidBlock {

    // 1.21: LiquidBlock takes a FlowingFluid instance directly (no Supplier).
    public ExampleFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(LEVEL, 8);
    }
}
