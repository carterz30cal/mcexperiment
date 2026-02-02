package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PetDrenchedPassive extends GameAbility {
    public PetDrenchedPassive() {

    }

    @Override
    public String name(GameAbility.AbilityContext context) {
        return "LIGHT_PURPLEPassive: Hunting Nemo";
    }

    @Override
    public List<String> description(GameAbility.AbilityContext context) {
        var list = new ArrayList<String>();
        list.add("GRAYDeal RED" + StringUtils.truncatedDouble2(getDamageBonus(context)) +"xGRAY more damage to fishing mobs.");
        return list;
    }

    private double getDamageBonus(GameAbility.AbilityContext context) {
        return 1.1 + (0.1 * context.level);
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (info.defender instanceof GameEnemy) {
            GameEnemy enemy = (GameEnemy) info.defender;
            if (enemy.hasTag("FISHING")) info.damage = (int) (info.damage * getDamageBonus(context));
        }
    }
}
