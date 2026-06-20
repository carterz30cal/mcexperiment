package com.carterz30cal.entities.enemies.abilities.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithDefend;
import com.carterz30cal.items.abilities.implementation.AbilityWithKillEffect;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Maxs out damage as a percentage of health, and rewards
 * players with coins for exceeding it.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyAbilityOverkill extends EnemyAbility implements AbilityWithDefend, AbilityWithKillEffect {
    protected final double percentage;
    protected final long reward;
    private final Map<GameEntity, Map<GamePlayer, Integer>> hits = new HashMap<>();

    public EnemyAbilityOverkill(ConfigurationSection section) {
        super(section);
        percentage = section.getDouble("percentage", 0.01D);
        reward = section.getLong("reward", 0L);
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        if (packet.aggressor instanceof GamePlayer player) {
            long cap = Math.round(((GameEnemy) context.getOwner()).getHealthSystem().getMaxHealth() * percentage);
            for (var dmg : packet.damages.entrySet()) {
                long value = dmg.getValue();
                if (value > cap) {
                    packet.damages.put(dmg.getKey(), cap);
                    player.coins += reward;
                    hits.putIfAbsent(context.getOwner(), new HashMap<>());
                    hits.get(context.getOwner()).compute(player, (k, v) -> (v == null) ? 1 : v + 1);
                }
            }
        }
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        var map = hits.getOrDefault(context.getOwner(), new HashMap<>());
        for (var player : map.keySet()) {
            int hits = map.get(player);
            player.sendMessage("<red><b>OVERKILL!</b> x" + hits + "<gold> - " + (hits * reward) + " coins</gold></red>");
        }
    }
}
