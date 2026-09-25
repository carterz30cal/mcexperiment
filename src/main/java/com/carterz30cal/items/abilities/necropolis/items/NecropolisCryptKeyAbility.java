package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.areas.bosses.necropolis.AreaCryptNecropolis;
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
public class NecropolisCryptKeyAbility extends GameAbility implements AbilityWithDescription, AbilityWithClick {
    private final AreaCryptNecropolis.NecropolisCryptTier tier;
    private final String correctItem;

    public NecropolisCryptKeyAbility(AreaCryptNecropolis.NecropolisCryptTier tier, String correctItem) {
        this.tier = tier;
        this.correctItem = correctItem;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Explorer";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var lore = "<grey>Head to the Crypt entrance in <white>Necropolis</white> and right click to open up a crypt instance, and optionally bring some friends!";
        return StringUtils.wrapText(lore, 50);
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation != Situation.RIGHT_CLICK || location == null) {
            return;
        }
        var player = context.getOwner();
        if (!AreaCryptNecropolis.REGISTRATION_BOX.getPlayersWithin().contains(player)) {
            player.sendMessage("<red>You need to be near to the crypt entrance in order to start a crypt instance!");
            return;
        }

        var held = ItemFactory.getItem(player.player.getInventory().getItemInMainHand());
        if (held.id.equals(correctItem)) {
            new AreaCryptNecropolis(player, tier);
            player.player.getInventory().getItemInMainHand().setAmount(player.player.getInventory().getItemInMainHand().getAmount() - 1);
        }
        else {
            player.sendMessage("<red>Please don't try and trick the system! Don't quickswap your items when using a consumable!");
        }
    }
}
