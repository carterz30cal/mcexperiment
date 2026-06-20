package com.carterz30cal.items.abilities.implementation;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithClick extends Ability {
    void click(PlayerAbilityContext context, Situation situation);

    enum Situation {
        LEFT_CLICK,
        RIGHT_CLICK
    }
}
