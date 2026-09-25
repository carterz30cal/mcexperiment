package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface DamageableEntity extends LocatableEntity, StatHavingEntity, TargetableEntity {
    void damage(@NotNull DamagePacket damagePacket);

    void heal(long amount);

    void kill();

    List<? extends ContextWithAbility<? extends GameEntity>> getDefensiveDamageModifiers();

    boolean isImmune(StatusEffect effect);

    boolean isAlive();

    /**
     *
     * @param by what is attempting to attack us
     * @return false if the victim is currently invulnerable, true otherwise
     */
    boolean isDamageable(AggressiveEntity by);

    double getHealthPercentage();

    long getHealth();
}
