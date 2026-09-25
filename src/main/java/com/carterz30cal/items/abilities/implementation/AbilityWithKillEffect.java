package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;

/**
 * Provides a killEffect() ability, which calls for player abilities when they kill an enemy
 * and calls for enemy abilities when they are killed.
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface AbilityWithKillEffect extends Ability {
    void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed);
}
