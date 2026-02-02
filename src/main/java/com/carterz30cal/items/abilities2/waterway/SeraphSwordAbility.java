package com.carterz30cal.items.abilities2.waterway;


import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.List;

public class SeraphSwordAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "AQUAStarlight-Imbued";
    }

    @Override
    public List<String> description(AbilityContext context) {
        var l = super.description(context);
        l.add("GRAYGains " + Stat.POWER.colour + "+5" + Stat.POWER.getIcon() + " GRAYfor every " + Stat.FOCUS.colour + "1" + Stat.FOCUS.getIcon() + " GRAYthat");
        l.add("GRAYyou have in total. ");
        return l;
    }

    @Override
    public void onItemStats(AbilityContext context, StatContainer item) {
        if (context == null || context.owner == null || context.owner.lastStats == null) {
            return;
        }
        item.scheduleOperation(Stat.POWER, StatOperationType.ADD, context.owner.lastStats.getStat(Stat.FOCUS) * 5);
    }
}
