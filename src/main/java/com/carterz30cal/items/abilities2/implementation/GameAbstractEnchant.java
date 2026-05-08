package com.carterz30cal.items.abilities2.implementation;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public abstract class GameAbstractEnchant extends GameAbility {
    private static final TextColor GOLD = TextColor.color(NamedTextColor.GOLD);
    private static final TextColor BLUE = TextColor.color(NamedTextColor.BLUE);
    private static final TextColor DARK_PURPLE = TextColor.color(NamedTextColor.DARK_PURPLE);

    @Override
    public TextColor colour(AbilityContext context) {
        if (context.level > getMaximumLevel()) {
            return GOLD;
        }
        else if (context.level == getMaximumLevel()) {
            return BLUE;
        }
        else {
            return DARK_PURPLE;
        }
    }
}
