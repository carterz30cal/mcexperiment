package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class LeafArmourSet extends ItemAbility {
    public LeafArmourSet(GamePlayer owner) {
        super(owner);
    }

    @Override
    public String name() {
        return "GREENLeaf Tank";
    }

    @Override
    public List<String> description()
    {
        List<String> l = new ArrayList<>();
        l.add("GRAYAll " + Stat.DEFENCE.getReverse() + " GRAYis converted");
        l.add("GRAYinto " + Stat.HEALTH.getReverse() + " GRAYat a GOLD1GRAY:GOLD4GRAY ratio.");
        return l;
    }

    @Override
    public void onPlayerStats(StatContainer item) {
        int additionalHealth = item.getStat(Stat.DEFENCE) * 4;
        //item.setStat(Stat.BONUS_COINS, 10000);
        item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, additionalHealth);
        item.scheduleOperation(Stat.DEFENCE, StatOperationType.CAP_MAX, 0);
    }
}
