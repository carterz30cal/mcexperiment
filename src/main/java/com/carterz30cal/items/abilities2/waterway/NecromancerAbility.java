package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class NecromancerAbility extends GameAbility {
    public NecromancerAbility() {

    }

    @Override
    public String name(AbilityContext context) {
        return "LIGHT_PURPLESoul Retrieval";
    }

    @Override
    public List<String> description(AbilityContext context) {
        List<String> desc = new ArrayList<>();
        desc.add("GRAYKilling enemies summons their soul to fight");
        desc.add("GRAYfor you. Each soul consumes " + Stat.MANA.getReverse() + " GRAYto keep existing.");

        return desc;
    }

    public void onKill(AbilityContext context, GameEnemy killed)
    {
        if (!(killed instanceof GameSummon)) GameSummon.spawnSummonFromEnemy(context.owner, killed);
    }
}
