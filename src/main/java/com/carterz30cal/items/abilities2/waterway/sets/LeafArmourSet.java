package com.carterz30cal.items.abilities2.waterway.sets;

import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class LeafArmourSet extends GameAbility {

    public LeafArmourSet() {

    }

    @Override
    public String name(AbilityContext context) {
        return "Leaf Tank";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>All of your " + Stat.DEFENCE.getReverse() + " is converted");
        list.add("<grey>into " + Stat.HEALTH.getReverse() + " at a ratio of <green>1</green><dark_grey>:</dark_grey><red>6</red>.");
        return list;
    }

    @Override
    public void onPlayerStats(AbilityContext context, StatContainer item) {
        int additionalHealth = item.getStat(Stat.DEFENCE) * 6;
        item.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, additionalHealth);
        item.scheduleOperation(Stat.DEFENCE, StatOperationType.CAP_MAX, 0);
    }
}
