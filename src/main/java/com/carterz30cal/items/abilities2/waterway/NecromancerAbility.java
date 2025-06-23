package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class NecromancerAbility extends ItemAbility {
    public NecromancerAbility(GamePlayer owner) {
        super(owner);
    }

    @Override
    public String name() {
        return "LIGHT_PURPLESoul Retrieval";
    }

    @Override
    public List<String> description() {
        List<String> desc = new ArrayList<>();
        desc.add("GRAYKilling enemies summons their soul to fight");
        desc.add("GRAYfor you. Each soul consumes " + Stat.MANA.getReverse() + " GRAYto keep existing.");

        return desc;
    }

    public void onKill(GameEnemy killed)
    {
        if (!(killed instanceof GameSummon)) GameSummon.spawnSummonFromEnemy(owner, killed);
    }
}
