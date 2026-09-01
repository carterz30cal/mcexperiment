package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SandShovelAbility extends GameAbility implements AbilityWithStats, AbilityWithDescription {

    @Override
    public String name(PlayerAbilityContext context) {
        return "Shovel for Victory!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var prog = context.getOwner() == null ? 0 : context.getOwner().getDiscoveryLevel(DiscoveryManager.get("sand_collection"));
        var text = "<grey>Each level of sand discovery obtained will grant an additional 15 " +
                Stat.MINING_FORTUNE.getReverse() +
                ". You are currently <green>Sand " + prog + "!";
        return StringUtils.wrapText(text, 36);
    }


    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) return;
        if (!(context.getOwner() instanceof GamePlayer player)) return;
        var prog = player.getDiscoveryLevel(DiscoveryManager.get("sand_collection"));
        stats.operation(new AddStatOperation(Stat.MINING_FORTUNE, 15L * prog));
    }
}
