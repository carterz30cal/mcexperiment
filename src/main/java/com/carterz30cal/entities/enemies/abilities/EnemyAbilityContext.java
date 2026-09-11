package com.carterz30cal.entities.enemies.abilities;

import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities.implementation.Ability;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class EnemyAbilityContext implements ContextWithAbility<GameEnemy> {
    private long level = 1;
    private final GameEnemy owner;
    private final EnemyAbility ability;

    public EnemyAbilityContext(EnemyAbility ability, GameEnemy owner) {
        this.owner = owner;
        this.ability = ability;
    }

    @Override
    public long getLevel() {
        return level;
    }

    @Override
    public Ability getAbility() {
        return ability;
    }

    @Override
    public GameEnemy getOwner() {
        return owner;
    }
}
