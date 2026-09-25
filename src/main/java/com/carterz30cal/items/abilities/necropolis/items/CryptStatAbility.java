package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.areas.bosses.necropolis.AreaCryptNecropolis;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.StatOperation;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Ability which only grants stats in a crypt
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class CryptStatAbility extends GameAbility implements AbilityWithStats, AbilityWithDescription {
    protected final StatOperation operation;
    protected final String description;
    protected final String name;

    public CryptStatAbility(String name, String description, StatOperation operation) {
        this.operation = operation;
        this.description = description;
        this.name = name;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) {
            return;
        }
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (!AreaCryptNecropolis.inCrypt(player)) {
            return;
        }
        stats.operation(operation);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        return StringUtils.wrapText("<grey>" + description, 45);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }
}
