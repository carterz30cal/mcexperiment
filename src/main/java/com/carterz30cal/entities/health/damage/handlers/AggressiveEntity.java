package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.StatHavingEntity;

import java.util.List;

public interface AggressiveEntity extends LocatableEntity, StatHavingEntity {
    List<DamageModifier> getAggressiveDamageModifiers();
}
