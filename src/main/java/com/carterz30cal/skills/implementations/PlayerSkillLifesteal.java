package com.carterz30cal.skills.implementations;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithStatusProc;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.GameSkill;
import com.carterz30cal.skills.SkillSoulType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class PlayerSkillLifesteal extends GameSkill implements AbilityWithStatusProc {
    public PlayerSkillLifesteal(SkillSoulType soulType, int maxLevel) {
        super(soulType, maxLevel);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = super.miniMessageDescription(context);
        lore.add("<grey>You will heal <red>" + healing(context.getLevel()) + "\u2665" + "</red> every time that you proc any status effect.");
        lore.add("<grey>This can activate as often as you can proc a status effect, and");
        lore.add("<grey>is additionally affected by any healing bonuses you may have.");
        return lore;
    }

    private long healing(long level) {
        return 4 + level;
    }

    @Override
    public long getSoulsNeedForLevel(long level) {
        return Math.round(100 * Math.pow(1.4, level));
    }

    @Override
    public void statusProcEffect(ContextWithAbility<? extends GameEntity> context, StatusEffect statusEffect, DamageableEntity victim) {
        if (context.getOwner() instanceof GamePlayer player) {
            player.heal(healing(context.getLevel()));
        }
    }
}
