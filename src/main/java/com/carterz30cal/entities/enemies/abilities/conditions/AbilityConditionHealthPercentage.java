package com.carterz30cal.entities.enemies.abilities.conditions;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.AbilityCondition;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class AbilityConditionHealthPercentage extends AbilityCondition {
    protected final double maxHealth;
    protected final double minHealth;

    public AbilityConditionHealthPercentage(ConfigurationSection section) {
        super(section);
        this.maxHealth = section.getDouble("max");
        this.minHealth = section.getDouble("min");
    }

    @Override
    public boolean hasConditionMet(ContextWithAbility<? extends GameEntity> context) {
        var enemy = (GameEnemy) context.getOwner();
        return enemy.getHealthPercentage() >= minHealth && enemy.getHealthPercentage() <= maxHealth;
    }
}
