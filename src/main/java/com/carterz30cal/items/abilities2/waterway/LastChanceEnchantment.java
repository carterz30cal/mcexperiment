package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class LastChanceEnchantment extends GameAbstractEnchant implements AbilityWithDescription, AbilityWithStats {


    public LastChanceEnchantment() {
        super("Last Chance", 4, ItemType.CHESTPLATE);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Last Chance";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var description = new ArrayList<String>();
        description.add("<grey>Grants " + formattedDisplay(Stat.DEFENCE, 10L * context.level) + " if you're below <red>15% " + Stat.HEALTH.getIcon() + "</red>");
        return description;
    }

    @Override
    public void onItemStats(PlayerAbilityContext context, StatContainer item) {

    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", 2 * context.level));
        if (context.level > 1) {
            reqs.add(new ItemReq("green_slime", context.level - 1));
        }
        return reqs;
    }

    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return 3 * context.getLevel();
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (context == null || !(context.getOwner() instanceof GamePlayer player) || situation != Situation.ITEM) {
            return;
        }
        if (player.healthSystem.getHealthPercentage() > 0.15) {
            return;
        }

        stats.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, 10 * context.getLevel());
    }
}
