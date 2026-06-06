package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.skills.SkillSoulType;
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
 * @version 4
 * @since 1.0.0
 */
public class EnemyData implements TagHavingEntity {
    public Map<DamageType, Long> damages = new HashMap<>();
    public Map<Stat, Long> stats = new HashMap<>();
    public Map<SkillSoulType, Long> souls = new HashMap<>();
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
        this.souls = new HashMap<>(existing.souls);
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
     * Sums every soul type that this enemy can drop.
     *
     * @return the total number of souls that this enemy will drop
     * @since 1.0.0
     */
    public final long getTotalSouls() {
        long totalSouls = 0;
        for (var s : souls.keySet()) {
            totalSouls += souls.get(s);
        }
        return totalSouls;
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
