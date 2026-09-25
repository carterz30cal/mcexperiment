package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.areas.bosses.necropolis.AreaMinibossNecropolisHydra;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class HydraKeyAbility extends GameAbility implements AbilityWithDescription, AbilityWithClick {
    private final AreaMinibossNecropolisHydra.HydraBossTier tier;
    private final String correctItem;

    public HydraKeyAbility(AreaMinibossNecropolisHydra.HydraBossTier tier, String correctItem) {
        this.tier = tier;
        this.correctItem = correctItem;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Great Hydra Hunt";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var lore = "<grey>Head to the Hydra Altar in <white>Necropolis</white> and click on the altar in order to start a " + tier.name
                + " fight! This item will be consumed on use and you may only start a fight if one is not already in progress.";
        return StringUtils.wrapText(lore, 50);
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation != Situation.RIGHT_CLICK || location == null || location.distanceSquared(AreaMinibossNecropolisHydra.ALTAR_LOCATION) > 4) {
            return;
        }

        var player = context.getOwner();
        var held = ItemFactory.getItem(player.player.getInventory().getItemInMainHand());
        if (held.id.equals(correctItem)) {
            boolean started;
            try {
                started = AreaMinibossNecropolisHydra.start(tier, player);
            } catch (IllegalStateException ignored) {
                return;
            }
            if (started) {
                player.player.getInventory().getItemInMainHand().setAmount(player.player.getInventory().getItemInMainHand().getAmount() - 1);
            }
            else {
                player.sendMessage("<red>Could not start a hydra fight at this time!");
            }
        }
        else {
            player.sendMessage("<red>Please don't try and trick the system! Don't quickswap your items when using a consumable!");
        }
    }
}
