package com.carterz30cal.items.abilities2.waterway.pets;

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
public class PetDrenchedActive extends GameAbility {
    public PetDrenchedActive() {

    }

    @Override
    public String name(GameAbility.AbilityContext context) {
        return "Active: Fishing 101 For Dummies";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>Gain " + formattedDisplay(Stat.FISHING_POWER, context.level + 1) + " for</grey>");
        list.add("<grey>every " + formattedDisplay(Stat.HEALTH, context.level + 15) + " that you have.</grey>");
        return list;
    }

    @Override
    public void onPlayerStats(GameAbility.AbilityContext context, StatContainer item) {
        item.scheduleOperation(Stat.FISHING_POWER,
                StatOperationType.ADD,
                ((context.level + 1) * item.getStat(Stat.HEALTH)) / (15D + context.level)
        );

    }
}
