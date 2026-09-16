package kasuga.lib.core.base.item_helper;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;

public class ExternalRemainderBlockItem extends BlockItem {

    @NotNull
    private final Supplier<Item> craftingRemainder;
    public ExternalRemainderBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        if (pProperties instanceof ExternalProperties externalProperties)
            craftingRemainder = externalProperties.craftingRemainderItem;
        else
            craftingRemainder = () -> null;
    }

    @NotNull
    public Supplier<Item> getCraftingRemainder() {
        return craftingRemainder;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return super.hasCraftingRemainingItem(stack) || craftingRemainder.get() != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack remain = super.getCraftingRemainingItem(itemStack);
        if ((remain == ItemStack.EMPTY || remain.is(Items.AIR)) && craftingRemainder.get() != null) {
            remain = craftingRemainder.get().getDefaultInstance();
        }
        return remain;
    }
}
