package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class RagingAxeAbility extends GameAbility implements AbilityWithStats, AbilityWithDescription {

    @Override
    public String name(PlayerAbilityContext context) {
        return "Rage!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = new ArrayList<String>();
        lore.add("<grey>This weapon gains " + formattedDisplay(Stat.POWER, 125) + " if you are below <red>250\u2665</red>.");
        return lore;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM || !(context.getOwner() instanceof DamageableEntity damageable)) {
            return;
        }
        if (damageable.getHealth() < 250) {
            stats.operation(new AddStatOperation(Stat.POWER, 125));
        }
    }
}
