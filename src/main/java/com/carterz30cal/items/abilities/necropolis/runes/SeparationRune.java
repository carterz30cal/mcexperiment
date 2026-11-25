package com.carterz30cal.items.abilities.necropolis.runes;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithDefend;
import com.carterz30cal.items.abilities.implementation.AbilityWithStats;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import com.carterz30cal.stats.operations.LegacyStatOperation;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SeparationRune extends GameRuneAbility implements AbilityWithDefend, AbilityWithStats {
    public SeparationRune() {
        super("Separation");
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        if (!(packet.defender instanceof GamePlayer player)) {
            return;
        }
        if (player.useMana(2L)) {
            var strength = player.getStat(Stat.STRENGTH);
            var mult = 1 - ((double) strength / (strength + 100D));
            packet.multiply(mult);
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var description =
                "<grey>Your <blue>Strength</blue> stat now also acts as an additional <green>Defence</green> stat, but you consume <light_purple>2♠ Mana</light_purple> per hit. If you " +
                        "run out of <light_purple>mana</light_purple>, you lose all of your <green>defence</green>. " +
                        "Your <blue>Strength</blue> will defend against all types of damage, minus <white>true</white> damage.";
        return StringUtils.wrapText(description, 30);
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.PLAYER || !(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (player.mana() < 2) {
            stats.operation(new LegacyStatOperation(StatOperationType.CAP_MAX, 0, Stat.DEFENCE));
        }
    }
}
