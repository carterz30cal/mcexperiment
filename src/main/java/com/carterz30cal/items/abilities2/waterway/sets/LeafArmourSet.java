package com.carterz30cal.items.abilities2.waterway.sets;

import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class LeafArmourSet extends GameAbility {

    public LeafArmourSet() {

    }

    @Override
    public String name(AbilityContext context) {
        return "GREENLeaf Tank";
    }

    @Override
    public List<String> description(AbilityContext context)
    {
        List<String> l = new ArrayList<>();
        l.add("GRAYAll " + Stat.DEFENCE.getReverse() + " GRAYis converted");
        l.add("GRAYinto " + Stat.HEALTH.getReverse() + " GRAYat a GOLD1GRAY:GOLD6GRAY ratio.");
        return l;
    }

    @Override
    public void onPlayerStats(AbilityContext context, StatContainer item) {
        int additionalHealth = item.getStat(Stat.DEFENCE) * 6;
        //item.setStat(Stat.BONUS_COINS, 10000);
        item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, additionalHealth);
        item.scheduleOperation(Stat.DEFENCE, StatOperationType.CAP_MAX, 0);
    }
}
