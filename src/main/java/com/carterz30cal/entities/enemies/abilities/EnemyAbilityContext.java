package com.carterz30cal.entities.enemies.abilities;

import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.Ability;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyAbilityContext implements ContextWithAbility<GameEnemy> {
    private long level = 1;
    private GameEnemy owner;

    @Override
    public long getLevel() {
        return level;
    }

    @Override
    public Ability getAbility() {
        return null;
    }

    @Override
    public GameEnemy getOwner() {
        return owner;
    }
}
