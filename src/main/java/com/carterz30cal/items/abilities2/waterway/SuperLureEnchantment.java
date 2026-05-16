package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.implementation.GameAbstractEnchant;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SuperLureEnchantment extends GameAbstractEnchant {
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
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>Grants " + formattedDisplay(Stat.FISHING_POWER, 10L * context.level)
                + " and also " + formattedDisplay(Stat.MANA, 15L * context.level) + "!");
        return list;
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
