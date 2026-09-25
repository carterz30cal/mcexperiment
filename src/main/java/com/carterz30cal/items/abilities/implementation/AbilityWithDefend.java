package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import org.jetbrains.annotations.NotNull;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithDefend extends Ability {
    void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet);
}
