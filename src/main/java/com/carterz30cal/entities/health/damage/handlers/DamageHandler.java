package com.carterz30cal.entities.health.damage.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Has the final say on how much damage an attack should deal to a
 * EntityHealthSystem. Can be used for 'hits'-phases or invulnerability
 * periods.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class DamageHandler implements DamageModifier {
    protected @Nullable DamageHandlers owner;

    public void setOwner(@NotNull DamageHandlers owner) {
        this.owner = owner;
    }
}
