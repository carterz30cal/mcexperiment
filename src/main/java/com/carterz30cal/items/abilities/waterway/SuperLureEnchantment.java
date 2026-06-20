package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SuperLureEnchantment extends GameAbstractEnchant implements AbilityWithDescription, AbilityWithStats {
    public SuperLureEnchantment() {
        super("Super Lure", 3, ItemType.ROD);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>Grants " + formattedDisplay(Stat.FISHING_POWER, 20L * context.level)
                + " and also " + formattedDisplay(Stat.FOCUS, 3L * context.level) + "!");
        return list;
    }

    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return 5 * context.getLevel();
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", (int) context.getLevel()));
        return reqs;
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (context == null || situation != Situation.ITEM) {
            return;
        }
        var level = context.getLevel();
        stats.scheduleOperation(Stat.FISHING_POWER, StatOperationType.ADD, 20 * level);
        stats.scheduleOperation(Stat.FOCUS, StatOperationType.ADD, 3 * level);
    }
}
