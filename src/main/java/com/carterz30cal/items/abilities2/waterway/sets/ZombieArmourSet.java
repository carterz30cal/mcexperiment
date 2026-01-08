package com.carterz30cal.items.abilities2.waterway.sets;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class ZombieArmourSet extends GameAbility {
    public ZombieArmourSet() {

    }

    @Override
    public String name(AbilityContext context) {
        return "GREENUndead Vitality";
    }

    @Override
    public List<String> description(AbilityContext context)
    {
        List<String> l = new ArrayList<>();
        if (context.owner == null || context.owner.lastStats == null) return l;

        l.add("GRAYWhenever an enemy hits you, heal RED" + getHealingPowerLast(context) + Stat.HEALTH.getIcon());
        l.add("DARK_GRAYScales with Vitality.");
        return l;
    }

    private long getHealingPowerLast(AbilityContext context) {
        return 10 + Math.round(context.owner.lastStats.getStat(Stat.VITALITY) / 10D);
    }
    private long getHealingPower(AbilityContext context) {
        return 10 + Math.round(context.owner.stats.getStat(Stat.VITALITY) / 10D);
    }

    @Override
    public int onDamaged(AbilityContext context, GameEnemy damager, int damage) {
        if (damager == null) return damage;

        context.owner.gainHealth((int) getHealingPower(context));

        return super.onDamaged(context, damager, damage);
    }
}
