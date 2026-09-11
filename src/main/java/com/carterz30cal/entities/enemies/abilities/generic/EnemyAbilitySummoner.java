package com.carterz30cal.entities.enemies.abilities.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.AbilityCondition;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.abilities.conditions.AbilityConditionAlwaysTrue;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities.implementation.AbilityWithTick;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyAbilitySummoner extends EnemyAbility implements AbilityWithTick {
    private static final Map<GameEnemy, List<GameEnemy>> summons = new HashMap<>();
    public EnemyBuilder type;
    public int maxSummons;
    public int spawnTime;
    public boolean allowRespawn;
    public double summonRange;
    public AbilityCondition condition;


    public EnemyAbilitySummoner(ConfigurationSection section) {
        super(section);
        this.type = EnemyBuilder.getBuilder(Objects.requireNonNull(section.getString("type")));
        this.maxSummons = section.getInt("max-summon-count");
        this.spawnTime = section.getInt("spawn-time", -1);
        this.allowRespawn = section.getBoolean("allow-respawn", false);
        this.summonRange = section.getDouble("summon-range", 5);
        if (section.contains("condition")) {
            this.condition = AbilityCondition.get(Objects.requireNonNull(section.getString("condition.class")), Objects.requireNonNull(section.getConfigurationSection("condition")));
        }
        else {
            this.condition = new AbilityConditionAlwaysTrue();
        }
    }

    protected List<GameEnemy> getSummons(GameEnemy owner) {
        summons.putIfAbsent(owner, new ArrayList<>());
        return summons.get(owner);
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        var owner = (GameEnemy) context.getOwner();
        var enemies = getSummons(owner);
        if (spawnTime != -1) {
            if (allowRespawn) {
                enemies.removeIf((e) -> e.dead);
            }
            if (condition.hasConditionMet(context) && tick % spawnTime == 0 && enemies.size() < maxSummons) {
                var loc = RandomUtils.getRandomInCircle(owner.getLocation(), 0, summonRange);
                var s = type.build(loc);
                s.register();
                enemies.add(s);
            }
        }
        summons.put(owner, enemies);
    }
}
