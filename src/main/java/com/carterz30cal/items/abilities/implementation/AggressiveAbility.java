package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;

public interface AggressiveAbility extends Ability {
    void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet);
}
