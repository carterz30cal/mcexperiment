package com.carterz30cal.skills.implementations;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.abilities2.implementation.AbilityWithStats;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities2.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.GameSkill;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provides a player skill that grants X amount of Y stat.
 * Nothing more!
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerSkillStat extends GameSkill implements AbilityWithStats {
    private final Stat stat;
    private final long perLevel;

    public PlayerSkillStat(SkillSoulType soulType, int maxLevel, Stat stat, long perLevel) {
        super(soulType, maxLevel);
        this.stat = stat;
        this.perLevel = perLevel;
    }

    @Override
    public long getSoulsNeedForLevel(long level) {
        return Math.round(100 * Math.pow(1.2, level));
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = super.miniMessageDescription(context);
        lore.add("<grey>Grants <" + stat.textColour.asHexString() + ">" + (Math.max(1, context.getLevel()) * perLevel) + stat.name + "<grey>!");
        return lore;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.PLAYER) {
            return;
        }

        stats.operation(new AddStatOperation(stat, context.getLevel() * perLevel));
    }
}
