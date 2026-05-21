package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AggressiveEntity extends LocatableEntity, StatHavingEntity {
    List<DamageModifier> getAggressiveDamageModifiers();

    DamagePacket getBlankDamagePacket();
}
