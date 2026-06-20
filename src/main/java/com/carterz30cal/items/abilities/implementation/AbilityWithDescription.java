package com.carterz30cal.items.abilities.implementation;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithDescription extends AbilityWithName {
    /**
     * Generate a list of component builders that provide descriptions on items.
     * Typically uncoloured, possibly decorated.
     * If this returns an empty list, then that means we want to use the miniMessageDescription method instead
     *
     * @param context required ability context for parametric descriptions.
     * @return a list of component builders
     * @since 1.0.0
     */
    default List<TextComponent.Builder> componentDescription(@NotNull PlayerAbilityContext context) {
        return new ArrayList<>();
    }

    /**
     * Generate a list of component builders that provide descriptions on items.
     * Typically used over componentDescription for simplicity.
     *
     * @param context required ability context for parametric descriptions.
     * @return a list of component builders
     * @since 1.0.0
     */
    default List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        return new ArrayList<>();
    }
}
