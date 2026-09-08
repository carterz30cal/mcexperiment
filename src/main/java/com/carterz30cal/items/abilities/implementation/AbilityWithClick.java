package com.carterz30cal.items.abilities.implementation;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface AbilityWithClick extends Ability {
    void click(PlayerAbilityContext context, Situation situation, @Nullable Location location);

    enum Situation {
        LEFT_CLICK,
        LEFT_CLICK_ARM_SWING,
        RIGHT_CLICK
    }
}
