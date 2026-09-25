package com.carterz30cal.entities.enemies.abilities.necropolis;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.generic.EnemyAbilitySummoner;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.items.abilities.implementation.AbilityWithDefend;
import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyAbilityHydraSummoner extends EnemyAbilitySummoner implements AbilityWithDefend, AbilityWithDescription {
    protected double reductionPerSummon;
    protected double teleportDistance;

    public EnemyAbilityHydraSummoner(ConfigurationSection section) {
        super(section);

        reductionPerSummon = section.getDouble("reduction-per-summon", 0);
        teleportDistance = section.getDouble("teleport-distance", 20);
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        if (!(context.getOwner() instanceof GameEnemy enemy)) {
            return;
        }
        var summon = getAliveSummons(enemy);
        if (summon == null) {
            return;
        }
        var reduction = Math.min(summon.count() * reductionPerSummon, 1);
        packet.multiply(1 - reduction);
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        super.tick(context, tick);
        if (!(context.getOwner() instanceof GameEnemy enemy)) {
            return;
        }
        var summon = getAliveSummons(enemy);
        if (summon == null) {
            return;
        }
        for (var s : summon.toList()) {
            if (s.distance(enemy.getLocation()) > teleportDistance) {
                s.teleport(enemy.getLocation());
            }
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        if (!(context.getOwner() instanceof GameEnemy enemy)) {
            return null;
        }
        var summon = getAliveSummons(enemy);
        if (summon == null) {
            return null;
        }
        var reduction = Math.min(summon.count() * reductionPerSummon, 1);
        return List.of("<b><green>Protected </green></b><gold>" + StringUtils.truncate(reduction * 100, 0) + "%");
    }
}
