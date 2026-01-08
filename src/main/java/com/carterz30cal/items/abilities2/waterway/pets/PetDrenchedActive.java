package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.List;

public class PetDrenchedActive extends GameAbility {
    public PetDrenchedActive() {

    }

    @Override
    public String name(GameAbility.AbilityContext context) {
        return "GOLDActive: Fishing 101 For Dummies";
    }

    @Override
    public List<String> description(GameAbility.AbilityContext context) {
        var list = new ArrayList<String>();
        list.add("GRAYGain " + display(Stat.FISHING_POWER, context.level + 1) + "GRAY for");
        list.add("GRAYevery " + display(Stat.HEALTH, 15 + context.level) + "GRAY you have.");
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
