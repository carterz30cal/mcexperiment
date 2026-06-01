package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.stats.Stat;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EnemyData implements TagHavingEntity {
    public Map<DamageType, Long> damages = new HashMap<>();
    public Map<Stat, Long> stats = new HashMap<>();
    public Set<String> tags;
    public Component name;
    public String mmName;
    public long level;
    public boolean alwaysDisplayHealth;
    public double coinMultiplier;
    public ItemLootTable lootTable;
    public String bestiaryCategory;
    public Sound hurtSound = Sound.ENTITY_ZOMBIE_HURT; // TODO add to files
    public Sound deathSound = Sound.ENTITY_ZOMBIE_DEATH;

    /**
     * Default empty constructor, doesn't set any fields.
     */
    public EnemyData() {

    }

    /**
     * Copy constructor
     *
     * @param existing our <code>EnemyData</code> to copy
     * @since 1.0.0
     */
    public EnemyData(EnemyData existing) {
        this.damages = new HashMap<>(existing.damages);
        this.stats = new HashMap<>(existing.stats);
        this.tags = new HashSet<>(existing.tags);
        this.name = existing.name;
        this.mmName = existing.mmName;
        this.level = existing.level;
        this.alwaysDisplayHealth = existing.alwaysDisplayHealth;
        this.coinMultiplier = existing.coinMultiplier;
        this.lootTable = existing.lootTable;
        this.bestiaryCategory = existing.bestiaryCategory;
        this.hurtSound = existing.hurtSound;
        this.deathSound = existing.deathSound;
    }

    /**
     * Calculates the total damage that this entity can deal, before any resistances.
     *
     * @return the total damage
     * @since 1.0.0
     */
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
