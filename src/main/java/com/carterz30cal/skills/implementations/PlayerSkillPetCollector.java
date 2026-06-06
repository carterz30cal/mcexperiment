package com.carterz30cal.skills.implementations;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
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
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerSkillPetCollector extends GameSkill implements AbilityWithStats {
    public PlayerSkillPetCollector(SkillSoulType soulType, int maxLevel) {
        super(soulType, maxLevel);
    }

    @Override
    public long getSoulsNeedForLevel(long level) {
        return Math.round(50 * Math.pow(1.8, level));
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (situation == Situation.PLAYER) {
            var count = player.pets.size();
            if (player.activePet != null) {
                count++;
            }
            stats.operation(new AddStatOperation(Stat.HEALTH, context.getLevel() * count));
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = super.miniMessageDescription(context);
        lore.add("<grey>You gain <red>" + context.getLevel() + Stat.HEALTH.name + "</red> per unique pet that");
        lore.add("<grey>you own and is present in your pets menu.");
        return lore;
    }
}
