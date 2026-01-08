package com.carterz30cal.items.abilities2.waterway;

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

public class SuperLureEnchantment extends GameAbility {
    public static final Set<ItemType> applicableTypes;
    static {
        applicableTypes = new HashSet<>();
        applicableTypes.add(ItemType.ROD);
    }
    @Override
    public String name(AbilityContext context) {
        return "Super Lure";
    }

    @Override
    public List<String> description(AbilityContext context) {
        List<String> lore = new ArrayList<>();
        lore.add("GRAYGrants " + display(Stat.FISHING_POWER, 10 * context.level) + "GRAY and");
        lore.add("GRAYalso grants " + display(Stat.MANA, 15 * context.level) + "GRAY!");
        return lore;
    }

    @Override
    public int getEnchantPower(AbilityContext context) {
        return 5 * context.level;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public List<ItemReq> getCatalystRequirements(AbilityContext context, int desiredLevel) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", desiredLevel));
        return reqs;
    }

    @Override
    public void onItemStats(AbilityContext context, StatContainer item) {
        item.scheduleOperation(Stat.FISHING_POWER, StatOperationType.ADD, 10 * context.level);
        item.scheduleOperation(Stat.MANA, StatOperationType.ADD, 15 * context.level);
    }

    @Override
    public Set<ItemType> getApplicableTypes() {
        return applicableTypes;
    }
}
