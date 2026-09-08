package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities.implementation.AbilityWithClick;
import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SeraphKeyAbility extends GameAbility implements AbilityWithDescription, AbilityWithClick {

    @Override
    public String name(PlayerAbilityContext context) {
        return "Waterway Seraph";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = new ArrayList<String>();
        lore.add("<grey>Head to the Seraph's Temple and <gold>right click</gold> at the altar");
        lore.add("<grey>to register your interest in fighting the Seraph. This");
        lore.add("<grey>will consume this key, and start the fight.");
        return lore;
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation != Situation.RIGHT_CLICK) {
            return;
        }

        var player = context.getOwner();
        var held = ItemFactory.getItem(player.player.getInventory().getItemInMainHand());
        if (held.id.equals("waterway_seraph_key")) {
            try {
                AreaBossWaterwaySeraph.instance.register(player);
            } catch (IllegalStateException ignored) {
                return;
            }
            player.player.getInventory().getItemInMainHand().setAmount(player.player.getInventory().getItemInMainHand().getAmount() - 1);
        }
        else {
            player.sendMessage("<red>Please don't swap off of the key this quickly!");
        }
    }
}
