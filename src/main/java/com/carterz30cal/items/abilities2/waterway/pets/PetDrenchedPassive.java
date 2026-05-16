package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.implementation.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PetDrenchedPassive extends GameAbility {
    public PetDrenchedPassive() {

    }

    @Override
    public String name(GameAbility.AbilityContext context) {
        return "Passive: Hunting Nemo";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>Deal <red>" + StringUtils.truncatedDouble2(getDamageBonus(context)) +
                "x</red> more damage to fishing mobs.");
        return list;
    }

    private double getDamageBonus(GameAbility.AbilityContext context) {
        return 1.1 + (0.1 * context.level);
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (info.defender instanceof GameEnemy enemy) {
            if (enemy.hasTag("FISHING")) info.damage = (int) (info.damage * getDamageBonus(context));
        }
    }
}
