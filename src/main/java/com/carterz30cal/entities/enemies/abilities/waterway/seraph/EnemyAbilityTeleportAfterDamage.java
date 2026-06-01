package com.carterz30cal.entities.enemies.abilities.waterway.seraph;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.items.abilities2.implementation.AbilityWithDefend;
import com.carterz30cal.items.abilities2.implementation.AbilityWithKillEffect;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implemented for the Waterway Seraph, so it can teleport out of danger.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyAbilityTeleportAfterDamage extends EnemyAbility implements AbilityWithDefend, AbilityWithKillEffect {
    private final Map<GameEntity, Long> counter = new HashMap<>();
    private final Set<DamageType> damageTypeFilter = new HashSet<>();
    private final long threshold;
    private Location location;

    public EnemyAbilityTeleportAfterDamage(long threshold, Location location) {
        super(null);
        this.location = location;
        this.threshold = threshold;
    }

    public EnemyAbilityTeleportAfterDamage(ConfigurationSection section) {
        super(section);
        threshold = 1000;
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        long total;
        if (damageTypeFilter.isEmpty()) {
            total = packet.getTotalDamage();
        }
        else {
            long count = 0;
            for (var type : damageTypeFilter) {
                count += packet.damages.getOrDefault(type, 0L);
            }
            total = count;
        }
        long amount = counter.getOrDefault(context.getOwner(), 0L) + total;
        if (amount > threshold) {
            ((LocatableEntity) context.getOwner()).teleport(location);
            counter.put(context.getOwner(), 0L);
        }
        else {
            counter.put(context.getOwner(), amount);
        }
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        counter.remove(context.getOwner());
    }
}
