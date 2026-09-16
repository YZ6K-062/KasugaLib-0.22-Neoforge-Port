package kasuga.lib.core.create;

import kasuga.lib.core.menu.locator.ContraptionBlockMenuLocator;
import kasuga.lib.core.menu.locator.MenuLocatorType;

/**
 * Create-flavour holder for menu locators that depend on Create's entity types.
 *
 * {@code ContraptionBlockMenuLocator} lives in the gated Create source set (it references
 * {@code AbstractContraptionEntity} / {@code MovementContext}), so its {@link MenuLocatorType}
 * cannot be declared on the always-compiled {@code MenuLocatorTypes}. This holder keeps
 * the locator's registration point reachable from Create-flavour code paths without polluting
 * the normal (non-Create) build classpath.
 */
public class CreateMenuLocators {
    public static final MenuLocatorType<ContraptionBlockMenuLocator> CONTRAPTION =
            new MenuLocatorType<>(ContraptionBlockMenuLocator::new);
}
