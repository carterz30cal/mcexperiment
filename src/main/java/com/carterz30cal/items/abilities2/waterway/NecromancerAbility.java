package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class NecromancerAbility extends GameAbility {
    public NecromancerAbility() {

    }

    @Override
    public String name(AbilityContext context) {
        return "Soul Retrieval";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>Killing enemies will summon their soul to fight");
        list.add("<grey>for your cause. Each soul consumes " + Stat.MANA.getReverse() + " to keep existing.");
        list.add("<dark_grey>Mana consumption scales with soul stats.");
        return list;
    }

    public void onKill(AbilityContext context, GameEnemy killed)
    {
        if (!(killed instanceof GameSummon)) {
            GameSummon.SpawnSummonFromEnemy(context.owner, killed);
        }
    }
}
