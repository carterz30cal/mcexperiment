package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerStatAbility extends GameAbility implements AbilityWithDescription, AbilityWithStats {
    private final String name;
    private final Stat stat;
    private final long amount;

    public PlayerStatAbility(String name, Stat stat, long amount) {
        this.stat = stat;
        this.amount = amount;
        this.name = name;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.PLAYER) return;

        stats.operation(new AddStatOperation(stat, amount));
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        return Collections.singletonList("<grey>Grants <" + stat.textColour.asHexString() + ">" + amount + stat.name + "</" + stat.textColour.asHexString() + "> to your person.");
    }
}
