package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.GameAbility;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class DeadAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "<red>INSTANT DEATH ABILITY</red>";
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (attacked instanceof GameEnemy) {
            ((GameEnemy) attacked).kill();
        }
        super.onAttack(context, info, attacked);
    }
}
