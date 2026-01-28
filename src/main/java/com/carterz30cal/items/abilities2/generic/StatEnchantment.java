package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatEnchantment extends GameAbility {
    public String enchantName;
    public int powerPerLevel;
    public Stat statGranted;
    public int flatStat;
    public int statPerLevel;
    public int maxLevel;
    public Set<ItemType> applicable;
    public StatOperationType statOperation;

    public StatEnchantment(String name, int powerPerLevel, Stat granted, int flat, int statPerLevel, int maxLevel, ItemType... types) {
        this.enchantName = name;
        this.powerPerLevel = powerPerLevel;
        this.statGranted = granted;
        this.flatStat = flat;
        this.statPerLevel = statPerLevel;
        this.maxLevel = maxLevel;
        this.applicable = new HashSet<>(List.of(types));
        this.statOperation = StatOperationType.ADD;
    }

    public StatEnchantment(String name, int powerPerLevel, Stat granted, int flat, int statPerLevel, int maxLevel, StatOperationType statOperation, ItemType... types) {
        this.enchantName = name;
        this.powerPerLevel = powerPerLevel;
        this.statGranted = granted;
        this.flatStat = flat;
        this.statPerLevel = statPerLevel;
        this.maxLevel = maxLevel;
        this.applicable = new HashSet<>(List.of(types));
        this.statOperation = statOperation;
    }

    @Override
    public String name(AbilityContext context) {
        return enchantName;
    }

    @Override
    public List<String> description(AbilityContext context) {
        List<String> lore = new ArrayList<>();
        if (statOperation == StatOperationType.MULTIPLY) {
            lore.add("GRAYGrants " + statGranted.colour + (getStat(context) > 0 ? "+" : "-") + getStat(context) + "% " + statGranted.getIcon() + "GRAY.");
        }
        else {
            lore.add("GRAYGrants " + display(statGranted, getStat(context)) + "GRAY.");
        }
        return lore;
    }

    @Override
    public int getEnchantPower(AbilityContext context) {
        return powerPerLevel * context.level;
    }

    @Override
    public int getMaximumLevel() {
        return maxLevel;
    }

    @Override
    public List<ItemReq> getCatalystRequirements(AbilityContext context, int desiredLevel) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", 1));
        return reqs;
    }

    @Override
    public void onItemStats(AbilityContext context, StatContainer item) {
        if (statOperation == StatOperationType.MULTIPLY) {
            item.scheduleOperation(statGranted, StatOperationType.MULTIPLY, (100 + getStat(context)) / 100D);
        }
        else {
            item.scheduleOperation(statGranted, statOperation, getStat(context));
        }
    }

    @Override
    public Set<ItemType> getApplicableTypes() {
        return applicable;
    }

    private int getStat(AbilityContext context) {
        return flatStat + (context.level * statPerLevel);
    }
}
