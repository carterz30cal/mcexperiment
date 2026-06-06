package com.carterz30cal.items.abilities2.waterway;


import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.GrantStatFromStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class SeraphSwordAbility extends GameAbility implements AbilityWithDescription, AbilityWithStats {
    @Override
    public String name(PlayerAbilityContext context) {
        return "Starlight-Imbued";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>Grants " + formattedDisplay(Stat.POWER, 5) + " for every<grey>");
        list.add("<grey>" + formattedDisplay(Stat.FOCUS, 2) + " that this weapon has.<grey>");
        return list;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) {
            return;
        }
        stats.operation(new GrantStatFromStatOperation(Stat.FOCUS, Stat.POWER, 2, 5));
    }
}
