package com.carterz30cal.entities.health.damage;

import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public enum StatusResistance {
    PHYSICAL(StatusEffect.BLEED, Stat.ROBUSTNESS),
    FIRE(StatusEffect.BURN, Stat.DOUSING, 0.1)
    ;
    private static final Map<StatusEffect, StatusResistance> damageToResistanceMap = new HashMap<>();
    private static final Map<StatusEffect, Stat> damageToStatMap = new HashMap<>();
    private static final Map<Stat, StatusEffect> statToDamageMap = new HashMap<>();

    static {
        for (var v : values()) {
            damageToResistanceMap.put(v.damageType, v);
            damageToStatMap.put(v.damageType, v.resistanceStat);
            statToDamageMap.put(v.resistanceStat, v.damageType);
        }
    }

    private final StatusEffect damageType;
    private final Stat resistanceStat;
    public final double resistanceMultiplier;

    StatusResistance(StatusEffect damageType, Stat resistanceStat) {
        this.damageType = damageType;
        this.resistanceStat = resistanceStat;
        this.resistanceMultiplier = 1;
    }

    StatusResistance(StatusEffect damageType, Stat resistanceStat, double resistanceMultiplier) {
        this.damageType = damageType;
        this.resistanceStat = resistanceStat;
        this.resistanceMultiplier = resistanceMultiplier;
    }


    @Nullable
    public static StatusEffect getStatusEffect(@NotNull Stat stat) {
        return statToDamageMap.getOrDefault(stat, null);
    }

    @Nullable
    public static Stat getStat(@NotNull StatusEffect damageType) {
        return damageToStatMap.getOrDefault(damageType, null);
    }

    public static StatusResistance getDamageResistance(StatusEffect damageType) {
        return damageToResistanceMap.get(damageType);
    }

    public StatusEffect getStatusEffect() {
        return damageType;
    }

    public Stat getResistanceStat() {
        return resistanceStat;
    }

}
