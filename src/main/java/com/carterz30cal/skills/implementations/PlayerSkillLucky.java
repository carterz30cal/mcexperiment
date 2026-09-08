package com.carterz30cal.skills.implementations;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithTick;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerSkillLucky extends PlayerSkillStat implements AbilityWithTick {
    protected long coinLoss;

    public PlayerSkillLucky(SkillSoulType soulType, int maxLevel, long perLevel, long coinLoss) {
        super(soulType, maxLevel, Stat.LUCK, perLevel, 1.5);
        this.coinLoss = coinLoss;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = "<grey>Grants <" + stat.textColour.asHexString() + ">" + (Math.max(1, context.getLevel()) * perLevel) + stat.name + "<grey>, but you lose <gold>" + coinLoss
                + " coins</gold> every minute. If you do not have enough coins, the luck vanishes until you're no longer broke.";
        return StringUtils.wrapText(lore, 50);
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (player.coins < coinLoss) {
            return;
        }
        super.modifyStats(context, stats, situation);
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (tick % (20 * 60) == 1) {
            player.takeCoins(coinLoss);
        }
    }
}
