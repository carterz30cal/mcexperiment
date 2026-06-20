package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface ContextWithAbility<E extends GameEntity> {
    long getLevel();

    Ability getAbility();

    E getOwner();
}
