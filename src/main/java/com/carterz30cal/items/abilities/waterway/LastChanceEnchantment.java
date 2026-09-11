package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.operations.AddStatOperation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class LastChanceEnchantment extends GameAbstractEnchant implements AbilityWithDescription, AbilityWithStats {


    public LastChanceEnchantment() {
        super("Last Chance", 4, ItemType.CHESTPLATE);
    }

    /**
     * @since 1.0.0 [1]
     */
    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var description = new ArrayList<String>();
        description.add("<grey>Grants " + formattedDisplay(Stat.DEFENCE, 10L * context.level) + " if you're below <red>15% " + Stat.HEALTH.getIcon() + "</red>");
        return description;
    }

    /**
     * @since 1.0.0 [1]
     */
    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", 2 * context.level));
        if (context.level > 1) {
            reqs.add(new ItemReq("green_slime", context.level - 1));
        }
        return reqs;
    }

    /**
     * @since 1.0.0 [1]
     */
    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return 3 * context.getLevel();
    }

    /**
     * @since 1.0.0 [1]
     */
    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (context == null || !(context.getOwner() instanceof GamePlayer player) || situation != Situation.ITEM) {
            return;
        }
        if (player.healthSystem.getHealthPercentage() > 0.15) {
            return;
        }

        stats.operation(new AddStatOperation(Stat.DEFENCE, 10 * context.getLevel()));
    }
}
