package com.carterz30cal.entities.health.status;

import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;

public class DamagingStatus extends AbstractStatus {

    public long damage;
    public double percentDamage;
    public DamageType damageType;

    public DamagingStatus(long damage, DamageType damageType) {
        this(damage, 0, damageType);
    }

    public DamagingStatus(double percentDamage, DamageType damageType) {
        this(0, percentDamage, damageType);
    }

    public DamagingStatus(long damage, double percentDamage, DamageType damageType) {
        this.damage = damage;
        this.percentDamage = percentDamage;
        this.damageType = damageType;
    }

    @Override
    public void apply(DamageableEntity entity) {
        long total = damage + Math.round(entity.getHealthPercentage() * percentDamage);
        var packet = new DamagePacket(entity, null, total, damageType);
        entity.damage(packet);
    }
}
