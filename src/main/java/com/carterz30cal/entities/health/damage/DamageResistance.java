package com.carterz30cal.entities.health.damage;

import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public enum DamageResistance {
    PHYSICAL(DamageType.PHYSICAL, Stat.DEFENCE),
    PROJECTILE(DamageType.PHYSICAL, Stat.DEFENCE),
    PROJECTILE_PADDING(DamageType.PHYSICAL, Stat.PADDING),
    ;
    private static final Map<DamageType, List<DamageResistance>> damageToResistanceMap = new HashMap<>();
    private static final Map<DamageType, Stat> damageToStatMap = new HashMap<>();
    private static final Map<Stat, DamageType> statToDamageMap = new HashMap<>();

    static {
        for (var v : values()) {
            damageToResistanceMap.putIfAbsent(v.damageType, new ArrayList<>());
            damageToResistanceMap.get(v.damageType).add(v);
            damageToStatMap.put(v.damageType, v.resistanceStat);
            statToDamageMap.put(v.resistanceStat, v.damageType);
        }
    }

    private final DamageType damageType;
    private final Stat resistanceStat;

    DamageResistance(DamageType damageType, Stat resistanceStat) {
        this.damageType = damageType;
        this.resistanceStat = resistanceStat;
    }

    @Nullable
    public static DamageType getDamageType(@NotNull Stat stat) {
        return statToDamageMap.getOrDefault(stat, null);
    }

    @Deprecated
    @Nullable
    public static Stat getStat(@NotNull DamageType damageType) {
        return damageToStatMap.getOrDefault(damageType, null);
    }

    public static List<DamageResistance> getDamageResistance(DamageType damageType) {
        return damageToResistanceMap.getOrDefault(damageType, new ArrayList<>());
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public Stat getResistanceStat() {
        return resistanceStat;
    }

}
