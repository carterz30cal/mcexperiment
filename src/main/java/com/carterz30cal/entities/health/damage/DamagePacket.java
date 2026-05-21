package com.carterz30cal.entities.health.damage;

import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.damage.operations.DamagePacketOperation;
import com.carterz30cal.entities.health.status.StatusEffect;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class DamagePacket {
    public final Map<DamageResistance, Long> resistances = new HashMap<>();
    public final Map<StatusEffect, Long> statusEffects = new HashMap<>();
    public final Map<StatusResistance, Long> statusResistances = new HashMap<>();
    private final List<DamagePacketOperation> operations = new ArrayList<>();
    public Map<DamageType, Long> damages = new HashMap<>();
    public DamageableEntity defender;
    public AggressiveEntity aggressor;

    public DamagePacket() {

    }
    public DamagePacket(DamageableEntity defender, AggressiveEntity aggressor, long damage) {
        this(defender, aggressor, damage, DamageType.PHYSICAL);
    }

    public DamagePacket(DamageableEntity defender, AggressiveEntity aggressor, long damage, DamageType damageType) {
        this.defender = defender;
        this.aggressor = aggressor;
        this.damages.put(damageType, damage);
        for (var resistance : DamageResistance.values()) {
            this.resistances.put(resistance, defender.getStat(resistance.getResistanceStat()));
        }
        for (var resistance : StatusResistance.values()) {
            this.statusResistances.put(resistance, defender.getStat(resistance.getResistanceStat()));
        }
    }

    public void addDamage(long damage) {
        addDamage(DamageType.PHYSICAL, damage);
    }

    public void addDamage(DamageType damageType, long damage) {
        var current = damages.getOrDefault(damageType, 0L);
        damages.put(damageType, current + damage);
    }

    public void addOperation(DamagePacketOperation operation) {
        operations.add(operation);
    }

    public void settle() {
        operations.sort(Comparator.comparing(DamagePacketOperation::getPriority));
        for (var operation : operations) {
            operation.run(this);
        }
    }

    /**
     * For resistances >= 0, this does x / x + 100
     * For resistances < 0, this does (1 - (x / 100))
     *
     * @param damageType what damage type are we asking for?
     * @return multiplier for damage
     * @since 1.0.0
     */
    public double getResistanceMultiplier(DamageType damageType) {
        var value = 0L;
        for (var resistance : DamageResistance.getDamageResistance(damageType)) {
            value += resistances.get(resistance);
        }
        if (value >= 0) {
            return value / (value + 100D);
        }
        else {
            return (1D - (value / 100D));
        }
    }

    public long getUnresistedStatusEffect(StatusEffect effect) {
        return Math.max(0, statusEffects.getOrDefault(effect, 0L) - statusResistances.getOrDefault(StatusResistance.getDamageResistance(effect), 0L));
    }

}
