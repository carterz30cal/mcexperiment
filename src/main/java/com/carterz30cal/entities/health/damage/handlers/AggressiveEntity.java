package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AggressiveEntity extends LocatableEntity, StatHavingEntity {
    List<? extends ContextWithAbility<? extends GameEntity>> getAggressiveDamageModifiers();

    /**
     * This just handles any post-attack events we want the entity to work with.
     * e.g. for projectiles this will destroy the projectile, for players this will
     * trigger attack cooldowns.
     */
    void attack();
    DamagePacket getBlankDamagePacket();
}
