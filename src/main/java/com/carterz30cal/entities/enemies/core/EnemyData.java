package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.stats.Stat;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class EnemyData implements TagHavingEntity {
    public Map<DamageType, Long> damages = new HashMap<>();
    public Map<Stat, Long> stats = new HashMap<>();
    public Set<String> tags;
    public Component name;
    public long level;
    public boolean alwaysDisplayHealth;
    public double coinMultiplier;
    public ItemLootTable lootTable;
    public String bestiaryCategory;
    public Sound hurtSound = Sound.ENTITY_ZOMBIE_HURT; // TODO add to files

    public long getTotalRawDamage() {
        long total = 0L;
        for (var l : damages.values()) total += l;
        return total;
    }

    @Override
    public boolean tag(@Nullable String tag) {
        return tags.contains(tag);
    }
}
