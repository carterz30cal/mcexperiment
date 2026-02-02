package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.abilities2.implementation.GameAbility;

public class DeadAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "REDINSTANT DEATH ABILITY";
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (attacked instanceof GameEnemy) {
            ((GameEnemy) attacked).kill();
        }
        super.onAttack(context, info, attacked);
    }
}
