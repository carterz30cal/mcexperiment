package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.status.StatusEffect;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithStatusProc extends Ability {
    void statusProcEffect(ContextWithAbility<? extends GameEntity> context, StatusEffect statusEffect, DamageableEntity victim);
}
