package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.summons.GameSummon;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class NecromancerAbility extends GameAbility implements AbilityWithDescription, AbilityWithKillEffect {
    public NecromancerAbility() {

    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Soul Hook";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>Killing enemies will summon their soul to fight");
        list.add("<grey>for your cause. Each soul consumes " + Stat.MANA.getReverse() + " to keep existing.");
        list.add("<dark_grey>Mana consumption scales with soul stats.");
        return list;
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            throw new IllegalStateException("NecromancerAbility must have a GamePlayer owner");
        }
        if (killed instanceof GameEnemy enemy && !(killed instanceof GameSummon)) {
            GameSummon.spawn(player, killed.getLocation(), enemy);
        }
    }
}
