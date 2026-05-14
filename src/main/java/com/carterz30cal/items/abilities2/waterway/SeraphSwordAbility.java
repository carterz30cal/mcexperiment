package com.carterz30cal.items.abilities2.waterway;


import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SeraphSwordAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "Starlight-Imbued";
    }

    @Override
    public List<TextComponent.Builder> componentDescription(@NotNull AbilityContext context) {
        var l = super.componentDescription(context);
        var l1 = text();
        l1.append(text("Gains", GRAY))
                .append(text(" +5" + Stat.POWER.getIcon(), Stat.POWER.textColour))
                .append(text(" for every", GRAY))
                .append(text(" 1" + Stat.FOCUS.getIcon(), Stat.FOCUS.textColour))
                .append(text(" that", GRAY));
        var l2 = text();
        l2.append(text("you have in total.", GRAY));
        l.add(l1);
        l.add(l2);
        return l;
    }

    @Override
    public void onItemStats(AbilityContext context, StatContainer item) {
        if (context == null || context.owner == null || context.owner.lastStats == null) {
            return;
        }
        item.scheduleOperation(Stat.POWER, StatOperationType.ADD, context.owner.lastStats.getStat(Stat.FOCUS) * 5);
    }
}
