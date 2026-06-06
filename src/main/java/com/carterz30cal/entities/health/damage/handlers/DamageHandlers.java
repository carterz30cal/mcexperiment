package com.carterz30cal.entities.health.damage.handlers;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public enum DamageHandlers {
    ;
    private final DamageHandler damageHandler;

    DamageHandlers(DamageHandler damageHandler) {
        this.damageHandler = damageHandler;
    }

    public DamageHandler getDamageHandler() {
        return damageHandler;
    }
}
