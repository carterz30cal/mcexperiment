package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;

/**
 * Abilities that have custom logic that isn't regularly implemented in a standard way.
 * Provides register() and deregister() hooks for creation/destruction.
 * Abilities are expected to provide their own tickers if they need them.
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface RegisterableAbility extends Ability {
    void register(ContextWithAbility<? extends GameEntity> context);

    void deregister(ContextWithAbility<? extends GameEntity> context);
}
