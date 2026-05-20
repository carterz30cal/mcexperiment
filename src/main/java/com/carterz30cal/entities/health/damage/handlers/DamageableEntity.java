package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.status.StatusEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface DamageableEntity extends LocatableEntity, StatHavingEntity, TargetableEntity {
    void damage(@NotNull DamagePacket damagePacket);

    void kill();

    List<DamageModifier> getDefensiveDamageModifiers();

    boolean isImmune(StatusEffect effect);

    double getHealthPercentage();
}
