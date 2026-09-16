package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.base.item_helper.ExternalProperties;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Item is an important basic element of minecraft.
 * @param <T> the class of your item.
 */
public class ItemReg<T extends Item> extends Reg {

    @Nullable public ResourceLocation model;
    private boolean customRender = false;
    private ItemBuilder<T> builder;

    public final Item.Properties properties = new ExternalProperties();
    private DeferredHolder<Item, T> registryObject = null;
    private MenuReg<?, ?> menuReg = null;
    private final List<TagKey<?>> tags;
    private final List<CreativeTabReg> creativeTabs = new ArrayList<>();
    boolean registerMenu = false;

    /**
     * Create an item reg.
     * <p>
     * Note: If you want to set a Crafting Remainder for your item, please use
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderItem} or
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderBlockItem}
     * <p>
     * then use {@link ExternalProperties#craftRemainder(Supplier)}
     * @param registrationKey the registration key of your item.
     * @param model If your item's model doesn't lie under the "namespace:models/item" folder, pass the location here,
     *              Pay attention that your model must be under the "namespace:models" folder. If your item's model just
     *              declared as usual, pass 'null' into this.
     */
    public ItemReg(String registrationKey, @Nullable @Deprecated ResourceLocation model) {
        super(registrationKey);
        this.model = model;
        this.tags = new ArrayList<>();
    }

    /**
     * If your don't need a customize model for your item, use this.
     * @param registrationKey the registration key of your item.
     */
    public ItemReg(String registrationKey) {
        super(registrationKey);
        this.model = null;
        this.tags = new ArrayList<>();
    }

    /**
     * See {@link BlockReg#defaultBlockItem(ResourceLocation)}
     * @param blockReg the block registration to create this item.
     * @param model the model location of this item.
     * @return self.
     */
    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg, @Nullable ResourceLocation model) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey, model);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    /**
     * See {@link BlockReg#defaultBlockItem()}
     * @param blockReg the block registration to create this item.
     * @return self.
     */
    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    /**
     * Pass the constructor lambda of your item here.
     * @param builder your item's constructor lambda.
     * @return self.
     */
    @Mandatory
    public ItemReg<T> itemType(ItemBuilder<? extends Item> builder) {
        this.builder = (ItemBuilder<T>) builder;
        return this;
    }

    /**
     * If your item's model doesn't lie under the "namespace:models/item" folder, pass the location here,
     * Pay attention that your model must be under the "namespace:models" folder. If your item's model just
     * declared as usual, pass 'null' into this.
     * @param location the resource location of your item's model.
     * @return self.
     */
    @Deprecated
    @Optional
    public ItemReg<T> model(ResourceLocation location) {
        this.model = location;
        return this;
    }

    /**
     * If you need your item to be custom rendered, use this.
     * See {@link kasuga.lib.core.base.CustomRenderedItem}
     * @param flag Should your item to be custom rendered.
     * @return self.
     */
    @Optional
    public ItemReg<T> shouldCustomRender(boolean flag) {
        customRender = flag;
        return this;
    }

    /**
     * Set the max stack size of your item.
     * @param size max stack size.
     * @return self.
     */
    @Optional
    public ItemReg<T> stackTo(int size) {
        properties.stacksTo(size);
        return this;
    }

    /**
     * Your item would be displayed in this tab.
     * @param tab the creative mode tab registration you want your item in.
     * @return self.
     */
    @Optional
    public ItemReg<T> tab(CreativeTabReg tab) {
        // 1.21: Item.Properties no longer carries a creative tab — entries are contributed when the tab
        // is opened. Remember the tab here and apply it in submit(), where the registry is available.
        if (tab != null) {
            creativeTabs.add(tab);
        }
        return this;
    }

    /**
     * Your item would be displayed in this tab.
     * @param tab the creative mode tab you want your item in.
     * @return self.
     */
    @Optional
    public ItemReg<T> tab(CreativeModeTab tab) {
        // Not supported on 1.21: vanilla tabs are addressed by ResourceKey and their contents can only be
        // extended through BuildCreativeModeTabContentsEvent, which a plain CreativeModeTab instance does not
        // carry. Use tab(CreativeTabReg) for tabs managed by KasugaLib.
        return this;
    }

    /**
     * Provide a menu for your item. Menus (and screens) are used for GUIs.
     * To use this, make sure your item is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param registrationKey the registration key of your menu.
     * @param menu the constructor function of your menu.
     * @param screen the constructor function of your screen.
     * @return self.
     * @param <F> your menu class.
     * @param <U> your screen class.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> ItemReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
     * Provide a menu for your item. See {@link MenuReg} for more info. To use this, make sure that your item
     * is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param menuReg your menu registration.
     * @return self.
     */
    @Optional
    public ItemReg<T> withMenu(MenuReg<?, ?> menuReg) {
        this.menuReg = menuReg;
        registerMenu = false;
        return this;
    }

    /**
     * Add an itemTag to your item. Item tag is a static attribute of your item.
     * @param tag Tags of your item.
     * @return self.
     */
    @Optional
    public ItemReg<T> withTag(TagKey<Item> tag) {
        tags.add(tag);
        return this;
    }

    /**
     * Customize your item's property.
     * @param identifier Item property customizer.
     * @return self.
     */
    @Optional
    public ItemReg<T> withProperty(PropertyIdentifier identifier) {
        identifier.apply(properties);
        return this;
    }

    /**
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public ItemReg<T> submit(SimpleRegistry registry) {
        if (builder == null) {
            crashOnNotPresent(Item.class, "itemType", "submit");
        }
        if(model != null) {
            registry.modelMappings().addMapping(
                    ResourceLocation.fromNamespaceAndPath(registry.namespace, "item/" + registrationKey + ".json"), model
            );
        }
        if(customRender)
            registry.stackCustomRenderedItemIn(this.registrationKey);
        registryObject = registry.item().register(registrationKey, () -> builder.build(properties));
        for (CreativeTabReg tab : creativeTabs) {
            // Resolved lazily when the tab is opened, so the holder may still be empty right now.
            registry.addItemToCreativeTab(tab.registrationKey, () -> getItem());
        }
        if(menuReg != null && registerMenu) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        return this;
    }

    public T getItem() {
        return registryObject == null ? null : registryObject.get();
    }

    public DeferredHolder<Item, T> getRegistryObject() {
        return registryObject;
    }

    public ResourceLocation getModelLocation() {
        return model;
    }

    ItemBuilder<T> type(){return builder;}

    public String getIdentifier() {
        return "item";
    }

    public interface PropertyIdentifier {
        void apply(Item.Properties properties);
    }

    public interface ItemBuilder<T extends Item> {
        T build(Item.Properties properties);
    }
}
