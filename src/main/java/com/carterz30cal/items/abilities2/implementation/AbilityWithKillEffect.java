package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithKillEffect extends Ability {
    void killEffect(PlayerAbilityContext context, DamageableEntity killed);
}
