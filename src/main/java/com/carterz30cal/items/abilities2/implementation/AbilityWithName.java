package com.carterz30cal.items.abilities2.implementation;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithName extends Ability {
    default String name(PlayerAbilityContext context) {
        return "<red>null</red>";
    }

    default TextColor colour(PlayerAbilityContext context) {
        return TextColor.color(NamedTextColor.LIGHT_PURPLE);
    }
}
