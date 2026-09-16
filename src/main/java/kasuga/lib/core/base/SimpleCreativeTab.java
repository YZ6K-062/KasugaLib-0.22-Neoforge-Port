package kasuga.lib.core.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is for CreativeModeTab functioning. Without this class we have to override the
 * icon builder over and over again only for apply our icons, which makes no scene.
 * <p>
 * 1.21 notes: {@link CreativeModeTab} no longer has a public {@code CreativeModeTab(String)} constructor
 * nor a {@code makeIcon()} hook — a tab is described by a {@link CreativeModeTab.Builder}. The label and
 * the icon supplier are therefore fed into that builder instead.
 * <p>
 * Items are contributed through the builder's {@code DisplayItemsGenerator}, which Minecraft only invokes
 * when the tab is actually opened. That makes {@link #addItem(Supplier)} safe to call at any point during
 * mod loading, including for items registered after this tab was constructed.
 */
public class SimpleCreativeTab extends CreativeModeTab {
    public final Supplier<ItemStack> icon;

    private final List<Supplier<? extends Item>> contents;

    /**
     * Use this to get a SimpleCreativeTab
     * @param label the name of your tab, usually a translation key.
     * @param icon the icon supplier. We would use this to get the icon automatically.
     */
    public SimpleCreativeTab(String label, @NotNull Supplier<ItemStack> icon) {
        this(label, icon, new ArrayList<>());
    }

    /**
     * The contents list is passed in rather than declared as a field initialiser: a lambda inside an
     * explicit {@code super(...)} argument may not reference {@code this}, so it captures the parameter
     * (effectively final) instead.
     */
    private SimpleCreativeTab(String label, @NotNull Supplier<ItemStack> icon,
                              List<Supplier<? extends Item>> contents) {
        super(CreativeModeTab.builder()
                .title(Component.translatable(label))
                .icon(icon)
                .displayItems((params, output) -> {
                    for (Supplier<? extends Item> supplier : contents) {
                        Item item = supplier.get();
                        if (item != null) {
                            output.accept(new ItemStack(item));
                        }
                    }
                }));
        this.icon = icon;
        this.contents = contents;
    }

    /**
     * Put an item into this tab. The supplier is resolved lazily when the tab is opened, so it is fine
     * to hand over a not-yet-registered item holder.
     */
    public void addItem(Supplier<? extends Item> item) {
        contents.add(item);
    }
}
