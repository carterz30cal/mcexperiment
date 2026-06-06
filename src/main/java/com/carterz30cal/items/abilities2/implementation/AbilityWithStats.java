package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.stats.StatContainer;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithStats extends Ability {
    void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation);

    enum Situation {
        ITEM,
        PLAYER,
        ENEMY,
        FINAL
    }
}
