package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generic class that grants a certain amount of a stat per player level.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GrantStatOnLevelAbility extends GameAbility
    implements AbilityWithDescription, AbilityWithStats
{
    private final String name;
    private final Stat stat;
    private final long amount;

    public GrantStatOnLevelAbility(String name, Stat stat, long amount) {
        this.name = name;
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) return;
        if (context.getOwner() instanceof GamePlayer player) {
            stats.operation(new AddStatOperation(stat, amount * player.getLevel()));
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var description = AbilityWithDescription.super.miniMessageDescription(context);
        long level = context.owner == null ? 0 : context.owner.getLevel();
        description.add("<grey>This item gains " + formattedDisplay(stat, amount) + " per player");
        description.add("<grey>level. Currently, this bonus is " + formattedDisplay(stat, amount * level));
        return description;
    }
}
