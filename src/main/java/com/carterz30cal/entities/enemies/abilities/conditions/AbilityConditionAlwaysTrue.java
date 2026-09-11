package com.carterz30cal.entities.enemies.abilities.conditions;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.AbilityCondition;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A conditional that always returns true, to avoid null cases.
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class AbilityConditionAlwaysTrue extends AbilityCondition {
    @Deprecated
    public AbilityConditionAlwaysTrue(ConfigurationSection section) {
        super(section);
    }

    /**
     * This condition needs no information
     *
     * @since 1.0.0 [2]
     */
    public AbilityConditionAlwaysTrue() {
        super(null);
    }

    @Override
    public boolean hasConditionMet(ContextWithAbility<? extends GameEntity> context) {
        return true;
    }
}
