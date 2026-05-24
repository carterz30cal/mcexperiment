package com.carterz30cal.entities.health;

import com.carterz30cal.entities.health.damage.handlers.DamageHandler;
import com.carterz30cal.entities.health.damage.handlers.DamageHandlers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EntityHealthSystemBuilder {
    private final List<DamageHandler> damageHandlers = new ArrayList<>();
    private long maxHealth;

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

    public EntityHealthSystem build() {
        var healthSystem = new EntityHealthSystem(maxHealth);
        healthSystem.addDamageHandlers(damageHandlers);
        healthSystem.setHealthPercentage(1);
        return healthSystem;
    }
}
