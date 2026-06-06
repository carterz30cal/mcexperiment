package com.carterz30cal.entities.health;

import com.carterz30cal.entities.health.damage.handlers.DamageHandler;
import com.carterz30cal.entities.health.damage.handlers.DamageHandlers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class EntityHealthSystemBuilder {
    private final List<DamageHandler> damageHandlers = new ArrayList<>();
    private long maxHealth;

    /**
     * Default blank constructor
     *
     * @since 1.0.0
     */
    public EntityHealthSystemBuilder() {

    }

    /**
     * Copy constructor
     *
     * @param existing what are we copying?
     * @since 1.0.0
     */
    public EntityHealthSystemBuilder(EntityHealthSystemBuilder existing) {
        maxHealth = existing.maxHealth;
    }

    public EntityHealthSystemBuilder addDamageHandler(DamageHandlers damageHandler) {
        damageHandlers.add(damageHandler.getDamageHandler());
        return this;
    }

    public EntityHealthSystemBuilder addDamageHandler(DamageHandler damageHandler) {
        damageHandlers.add(damageHandler);
        return this;
    }

    public EntityHealthSystemBuilder setMaxHealth(long maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    public EntityHealthSystem build() {
        var healthSystem = new EntityHealthSystem(maxHealth);
        healthSystem.addDamageHandlers(damageHandlers);
        healthSystem.setHealthPercentage(1);
        return healthSystem;
    }
}
