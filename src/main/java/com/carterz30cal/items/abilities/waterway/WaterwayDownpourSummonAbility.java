package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.areas.areas.GameAreaWaterway;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities.implementation.AbilityWithClick;
import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Allows you to bring forward the Waterway Downpour event.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class WaterwayDownpourSummonAbility extends GameAbility implements AbilityWithDescription, AbilityWithClick {

    @Override
    public String name(PlayerAbilityContext context) {
        return "Storm on Demand";
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation != Situation.RIGHT_CLICK) return;
        var check = ItemFactory.getItem(context.owner.getMainItem());
        if (check.abilities.stream().noneMatch(a -> a.ability instanceof WaterwayDownpourSummonAbility)) return;
        var rain = GameAreaWaterway.downpour;
        if (rain == null || (rain.active() && rain.duration() < 1)) context.owner.sendMessage("<red>There's no downpour on the calendar!");
        else if (rain.active()) context.owner.sendMessage("<red>It's already raining!");
        else {
            rain.start();
            context.owner.getMainItem().setAmount(context.owner.getMainItem().getAmount() - 1);
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var desc = "<grey>Right click with this to bring forward the rains of <blue>Waterway</blue>, which allows unique mobs to spawn for a short while!";
        return StringUtils.wrapText(desc, 32);
    }
}
