package com.carterz30cal.entities.health.status;

import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;

public abstract class AbstractStatus {
    @Deprecated
    public void onProc(GameEnemy enemy) {
    }

    public abstract void apply(DamageableEntity entity);
}
