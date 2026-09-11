package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.areas.Areas;
import com.carterz30cal.areas.areas.GameAreaWaterway;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.StatOperation;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A generic ability that grants stats when the Waterway Downpour event is active.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class StatOperationOnRainAbility extends GameAbility implements AbilityWithStats, AbilityWithDescription {
    private final String name;
    private final Stat stat;
    private final StatOperation operation;
    private final boolean raining;

    public StatOperationOnRainAbility(String name, Stat stat, StatOperation operation) {
        this.name = name;
        this.stat = stat;
        this.operation = operation;
        this.raining = true;
    }

    public StatOperationOnRainAbility(String name, Stat stat, StatOperation operation, boolean raining) {
        this.name = name;
        this.stat = stat;
        this.operation = operation;
        this.raining = raining;
    }


    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) return;
        if (context.getOwner() instanceof GamePlayer player) {
            if (player.area != Areas.WATERWAY || GameAreaWaterway.downpour == null) return;
            boolean active = GameAreaWaterway.downpour.active();
            if (active == raining) {
                stats.operation(operation);
            }
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var description = raining ? "<grey>When raining" : "<grey>When not raining";
        description += ", grant " + operation.display() + "<grey>.";
        return StringUtils.wrapText(description, 40);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }
}
