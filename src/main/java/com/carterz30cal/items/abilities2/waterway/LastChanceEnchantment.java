package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LastChanceEnchantment extends GameAbility {
    public static final Set<ItemType> applicableTypes;

    static {
        applicableTypes = new HashSet<>();
        applicableTypes.add(ItemType.HELMET);
        applicableTypes.add(ItemType.CHESTPLATE);
        applicableTypes.add(ItemType.LEGGINGS);
        applicableTypes.add(ItemType.BOOTS);
    }

    @Override
    public String name(AbilityContext context) {
        return "Last Chance";
    }

    @Override
    public List<String> description(AbilityContext context) {
        var l = super.description(context);
        l.add("GRAYGrants " + display(Stat.DEFENCE, 10 * context.level) + " GRAY if you're below RED15% " + Stat.HEALTH.getIcon());
        return l;
    }

    @Override
    public void onItemStats(AbilityContext context, StatContainer item) {
        if (context != null && context.owner != null && context.owner.health <= 0.15) {
            item.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, 10 * context.level);
        }
    }

    @Override
    public List<ItemReq> getCatalystRequirements(AbilityContext context, int desiredLevel) {
        List<ItemReq> reqs = super.getCatalystRequirements(context, desiredLevel);
        reqs.add(new ItemReq("combination_catalyst_shard", 2 * context.level));
        if (context.level > 1) {
            reqs.add(new ItemReq("green_slime", context.level - 1));
        }
        return reqs;
    }

    @Override
    public int getEnchantPower(AbilityContext context) {
        return 3 * context.level;
    }

    @Override
    public int getMaximumLevel() {
        return 2;
    }

    @Override
    public Set<ItemType> getApplicableTypes() {
        return applicableTypes;
    }
}
