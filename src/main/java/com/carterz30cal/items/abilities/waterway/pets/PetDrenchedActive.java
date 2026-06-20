package com.carterz30cal.items.abilities.waterway.pets;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.abilities.implementation.*;
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
public class PetDrenchedActive extends GameAbility implements AbilityWithDescription, AbilityWithStats {
    public PetDrenchedActive() {

    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Active: Fishing 101 For Dummies";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>Gain " + formattedDisplay(Stat.FISHING_POWER, context.level + 1) + " for</grey>");
        list.add("<grey>every " + formattedDisplay(Stat.HEALTH, context.level + 15) + " that you have.</grey>");
        return list;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.PLAYER) {
            return;
        }
        stats.scheduleOperation(Stat.FISHING_POWER,
                StatOperationType.ADD,
                ((context.getLevel() + 1) * stats.stat(Stat.HEALTH)) / (15D + context.getLevel())
        );
    }
}
