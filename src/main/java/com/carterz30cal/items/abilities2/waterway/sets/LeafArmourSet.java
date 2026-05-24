package com.carterz30cal.items.abilities2.waterway.sets;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class LeafArmourSet extends GameAbility implements AbilityWithDescription, AbilityWithStats {

    public LeafArmourSet() {

    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Leaf Tank";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>All of your " + Stat.DEFENCE.getReverse() + " is converted");
        list.add("<grey>into " + Stat.HEALTH.getReverse() + " at a ratio of <green>1</green><dark_grey>:</dark_grey><red>6</red>.");
        return list;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.PLAYER) {
            return;
        }
        long additionalHealth = stats.stat(Stat.DEFENCE) * 6;
        stats.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, additionalHealth);
        stats.scheduleOperation(Stat.DEFENCE, StatOperationType.CAP_MAX, 0);
    }
}
